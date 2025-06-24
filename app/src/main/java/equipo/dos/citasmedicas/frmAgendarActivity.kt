package equipo.dos.citasmedicas

import Persistencia.AdapterMedico
import Persistencia.fakebd
import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding
import java.io.Serializable

class frmAgendarActivity : AppCompatActivity() {

    var adapter: AdapterMedico?= null
    private val binding by lazy {
        ActivityFrmPrincipalBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frm_agendar)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_agendar)
        val toolbar = findViewById<Button>(R.id.btnMenu)
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

        btnPerfil.setOnClickListener{
            var inte : Intent = Intent(this, frmMiPerfilActivity::class.java)
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(inte)
            true
        }

        btnMenuCerrar.setOnClickListener{
            drawerLayout.closeDrawer(GravityCompat.START)
        }


        //adapter
        adapter = AdapterMedico(this, fakebd.medicos)
        var listaMedicos: ListView= findViewById(R.id.lvMedicos)
        listaMedicos.adapter=adapter



    }
}




