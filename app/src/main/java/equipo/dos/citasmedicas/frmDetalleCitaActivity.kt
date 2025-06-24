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

        cancelar.setOnClickListener {
            mostrarDialogDeCancelacion()
        }

        nm.text = intent.getStringExtra("nombre")
        esp.text = intent.getStringExtra("especialidad")
        fecha.text = intent.getStringExtra("fecha")
        hora.text = intent.getStringExtra("hora")
        motivo.text = intent.getStringExtra("motivo")

        val estadoCita = intent.getStringExtra("estado")
        estado.text = estadoCita

        when (estadoCita) {
            "Pendiente" -> {
                seccion.visibility = View.GONE
                cancelar.visibility = View.VISIBLE
            }
            "Completada" -> {
                seccion.visibility = View.VISIBLE
                cancelar.visibility = View.GONE
            }
            "Cancelada" -> {
                seccion.visibility = View.GONE
                cancelar.visibility = View.GONE
            }
            else -> {
                seccion.visibility = View.GONE
                cancelar.visibility = View.GONE
            }
        }
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_detalle_cita)
        val toolbar = findViewById<Button>(R.id.btnMenu)
        val nav = findViewById<NavigationView>(R.id.navegacion_menu)

        toolbar.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val menu = nav.menu
        val opcion = menu.findItem(R.id.btnOpcion)

        if (sesion.tipoSesion() == "paciente") {
            opcion.setTitle("Agendar")
            opcion.setIcon(R.drawable.date48)
        } else {
            opcion.setTitle("Ajustar Consulta")
            opcion.setIcon(R.drawable.settings30)
        }

        nav.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnMenuMisCitas -> {
                    var inte: Intent = Intent(this, frmPrincipalActivity::class.java)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(inte)
                    true
                }

                R.id.btnMenuHistorial -> {
                    var inte: Intent
                    inte = Intent(this, frmHistorialActivity::class.java)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(inte)
                    true
                }

                R.id.btnOpcion -> {
                    var inte: Intent
                    if (sesion.tipoSesion() == "paciente") {
                        inte = Intent(this, frmAgendarActivity::class.java)
                    } else {
                        inte = Intent(this, AjustesConsultaActivity::class.java)
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(inte)
                    true
                }

                R.id.btnMenuCerrarSesion -> {
                    var inte: Intent = Intent(this, frmLoginActivity::class.java)
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
    private fun mostrarDialogDeCancelacion() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_cancelar_cita)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnConfirmar = dialog.findViewById<Button>(R.id.btnConfirmarCancelacion)
        val btnAtras = dialog.findViewById<Button>(R.id.btnAtrasCancelacion)

        btnConfirmar.setOnClickListener {
            dialog.dismiss()
            actualizarCancelado()
        }

        btnAtras.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun actualizarCancelado() {
        val estado: TextView = findViewById(R.id.tvEstadoD)
        val seccion: LinearLayout = findViewById(R.id.sdr)
        val cancelar: TextView = findViewById(R.id.btnCancelarCita)

        estado.text = "Cancelada"
        seccion.visibility = View.GONE
        cancelar.visibility = View.GONE

        Toast.makeText(this, "Cita cancelada", Toast.LENGTH_SHORT).show()
    }
}

