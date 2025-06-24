package equipo.dos.citasmedicas

import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding

class frmDetalleCitaActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityFrmPrincipalBinding.inflate(layoutInflater)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_detalle_cita)

        val nm : TextView = findViewById(R.id.tvMedicoD)
        val esp : TextView = findViewById(R.id.tvEspecialidadD)
        val fecha : TextView = findViewById(R.id.tvFechaD)
        val hora : TextView = findViewById(R.id.tvHoraD)
        val estado : TextView = findViewById(R.id.tvEstadoD)
        val motivo : TextView = findViewById(R.id.tvMotivoD)
        val seccion : LinearLayout = findViewById(R.id.sdr)
        val receta : ImageView = findViewById(R.id.ivRecetaDetallesCita)
        val cancelar : TextView = findViewById(R.id.btnCancelarCita)

        val imgFotoPerfil = findViewById<ShapeableImageView>(R.id.imgFotoPerfil)

        nm.setText(intent.getStringExtra("nombre"))
        esp.setText(intent.getStringExtra("especialidad"))
        fecha.setText(intent.getStringExtra("fecha"))
        hora.setText(intent.getStringExtra("hora"))
        estado.setText(intent.getStringExtra("estado"))
        motivo.setText(intent.getStringExtra("motivo"))

        if (intent.getStringExtra("estado") != "Completada"){
            seccion.visibility = View.GONE
            cancelar.visibility = View.GONE
        }

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_detalle_cita)
        val toolbar = findViewById<Button>(R.id.btnMenu)
        val nav = findViewById<NavigationView>(R.id.navegacion_menu)

        toolbar.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val menu = nav.menu
        val opcion = menu.findItem(R.id.btnMenuAjusteConsulta)

        opcion.isVisible = false

        nav.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnMenuMisCitas -> {
                    var inte : Intent = Intent(this, frmPrincipalActivity::class.java)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(inte)
                    true
                }
                R.id.btnMenuHistorial -> {
                    var inte : Intent
                    inte = Intent(this, frmHistorialActivity::class.java)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(inte)
                    true
                }
                R.id.btnMenuAjusteConsulta -> {
                    var inte : Intent
                    inte = Intent(this, AjustesConsultaActivity::class.java)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(inte)
                    true
                }
                R.id.btnMenuCerrarSesion -> {
                    var inte : Intent = Intent(this, frmLoginActivity::class.java)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    sesion.cerrarSesion()
                    startActivity(inte)
                    true
                }
                else -> false
            }
        }

        val headerView = nav.getHeaderView(0)

        val btnPerfil = headerView.findViewById<ImageView>(R.id.btnPerfil)
        val btnMenuCerrar = headerView.findViewById<Button>(R.id.btnMenuCerrarMenu)

        // cargar imagen de perfil
        val sesionActual = sesion.obtenerSesion()
        if (sesionActual != null) {
            val fotoNombre = when (sesionActual) {
                is paciente -> sesionActual.fotoPerfil
                is medico -> sesionActual.fotoPerfil
                else -> null
            }

            fotoNombre?.let {
                val resId = resources.getIdentifier(it, "drawable", packageName)
                if (resId != 0) {
                    btnPerfil.setImageResource(resId)
                }
            }
        }


        btnPerfil.setOnClickListener{
            var inte : Intent = Intent(this, frmMiPerfilActivity::class.java)
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(inte)
            true
        }

        btnMenuCerrar.setOnClickListener{
            drawerLayout.closeDrawer(GravityCompat.START)
        }

    }
}
