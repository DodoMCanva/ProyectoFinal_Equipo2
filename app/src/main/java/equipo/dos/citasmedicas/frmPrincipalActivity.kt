package equipo.dos.citasmedicas

import Persistencia.AdapterCita
import Persistencia.AdapterMedico
import Persistencia.fakebd
import Persistencia.medico
import Persistencia.paciente
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding

class frmPrincipalActivity : AppCompatActivity() {

    var adapter:AdapterCita? = null

    private val binding by lazy {
        ActivityFrmPrincipalBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frm_principal)



        val sesion = intent.getSerializableExtra("sesion")
        val tipoSesion: String = when (sesion) {
            is paciente -> "paciente"
            is medico -> "medico"
            else -> "no asignado"
        }

        adapter= AdapterCita(this, fakebd.citas, tipoSesion)
        var listaCitas: ListView = findViewById(R.id.lvCitas)
        listaCitas.adapter=adapter

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_miscitas)
        val toolbar = findViewById<Button>(R.id.btnMenu)
        val nav = findViewById<NavigationView>(R.id.navegacion_menu)


        toolbar.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val menu = nav.menu
        val opcion = menu.findItem(R.id.btnMenuOpcion)

        if (tipoSesion == "paciente") {
            opcion.setIcon(R.drawable.date48)
            opcion.title = "Agendar"
        } else {
            opcion.setIcon(R.drawable.settings30)
            opcion.title = "Ajustes de Consulta"
        }

        nav.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnMenuMisCitas -> {
                    var inte : Intent = Intent(this, frmPrincipalActivity::class.java)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(inte)
                    true
                }
                R.id.btnMenuOpcion -> {
                    var inte : Intent
                    if (tipoSesion == "paciente") {
                        inte = Intent(this, frmAgendarActivity::class.java)
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
        }

    }
}
