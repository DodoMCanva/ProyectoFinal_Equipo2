package equipo.dos.citasmedicas

import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import equipo.dos.citasmedicas.databinding.ActivityFrmMiPerfilBinding
import equipo.dos.citasmedicas.databinding.ActivityFrmPerfilBinding
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding

class frmMiPerfilActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityFrmMiPerfilBinding.inflate(layoutInflater)
    }

    private lateinit var imgFotoPerfil: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frm_mi_perfil)

        imgFotoPerfil = findViewById(R.id.imgFotoPerfil)

        var p : paciente? = null
        var m : medico? = null

        val nombre : TextView = findViewById(R.id.perfilNombre)
        val numero : TextView = findViewById(R.id.perfilNumero)
        val fecha : TextView = findViewById(R.id.perfilFechaNa)
        val genero : TextView = findViewById(R.id.perfilGenero)
        val cerrar : TextView = findViewById(R.id.btnCerrarSesion)
        val editar : TextView = findViewById(R.id.btnEditarPerfil)


        //
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_mi_perfil)
        val toolbar = findViewById<Button>(R.id.btnMenu)
        val nav = findViewById<NavigationView>(R.id.navegacion_menu)

        toolbar.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val menu = nav.menu
        val opcion = menu.findItem(R.id.btnMenuAjusteConsulta)

        when (val s = sesion.obtenerSesion()) {
            is paciente -> {
                nombre.text = s.nombre
                numero.text = s.correo
                fecha.text = s.fechaNacimiento
                genero.text = s.genero
                opcion.isVisible = false
                val fotoResId = resources.getIdentifier(s.fotoPerfil, "drawable", packageName)
                if (fotoResId != 0) {
                    imgFotoPerfil.setImageResource(fotoResId)
                }
            }
            is medico -> {
                nombre.text = s.nombre
                numero.text = s.correo
                fecha.text = s.fechaNacimiento
                genero.text = s.genero
                val fotoResId = resources.getIdentifier(s.fotoPerfil, "drawable", packageName)
                if (fotoResId != 0) {
                    imgFotoPerfil.setImageResource(fotoResId)
                }
            }
            else -> {
                Toast.makeText(this, "no se cargo correctamente la sesion", Toast.LENGTH_SHORT).show()
            }
        }

        editar.setOnClickListener(){
            var inte : Intent = Intent(this, frmEditarActivity::class.java)
            startActivity(inte)
        }
        cerrar.setOnClickListener(){
            val inte = Intent(this, frmLoginActivity::class.java)
            inte.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            sesion.cerrarSesion()
            startActivity(inte)
            finish()
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
            val intent = Intent(this, frmLoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            sesion.cerrarSesion()
            startActivity(intent)
        }

    }
}
