package equipo.dos.citasmedicas

import Persistencia.AdapterCita
import Persistencia.fakebd
import Persistencia.sesion
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import equipo.dos.citasmedicas.databinding.ActivityFrmHistorialBinding
import java.util.Calendar

class frmHistorialActivity : AppCompatActivity() {
    var adapter:AdapterCita? = null

    private val binding by lazy {
        ActivityFrmHistorialBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frm_historial)

        adapter = AdapterCita(this, fakebd.citas, sesion.tipoSesion())
        var listaCitas = findViewById<ListView>(R.id.lvHistorial)
        listaCitas.adapter = adapter

        val toolbar = findViewById<Button>(R.id.btnMenu)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_miscitas)
        val nav = findViewById<NavigationView>(R.id.navegacion_menu)

        toolbar.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val menu = nav.menu
        val opcion = menu.findItem(R.id.btnMenuAjusteConsulta)

        if (sesion.tipoSesion() == "paciente") {
            opcion.isVisible = false
        }

        nav.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnMenuMisCitas -> {
                    var inte : Intent = Intent(this, frmPrincipalActivity::class.java)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(inte)
                    true
                }
                R.id.btnMenuAjusteConsulta -> {
                    var inte : Intent
                    if (sesion.tipoSesion() == "paciente") {
                        inte = Intent(this, frmHistorialActivity::class.java)
                    } else {
                        inte = Intent(this, AjustesConsultaActivity::class.java)
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(inte)
                    true
                }
                R.id.btnMenuCerrarSesion -> {
                    var inte : Intent = Intent(this, frmLoginActivity::class.java)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(inte)
                    true
                }
                else -> false
            }
        }

        val headerView = nav.getHeaderView(0)
        val btnPerfil = headerView.findViewById<ImageView>(R.id.btnPerfil)
        val btnMenuCerrar = headerView.findViewById<Button>(R.id.btnMenuCerrarMenu)

        btnPerfil.setOnClickListener{
            var inte : Intent = Intent(this, frmMiPerfilActivity::class.java)
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(inte)
            true
        }

        btnMenuCerrar.setOnClickListener{
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


    }
}