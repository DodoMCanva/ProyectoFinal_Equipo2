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
import equipo.dos.citasmedicas.helpers.MenuDesplegable

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

        when (val s = sesion.obtenerSesion()) {
            is paciente -> {
                nombre.text = s.nombre
                numero.text = s.correo
                fecha.text = s.fechaNacimiento
                genero.text = s.genero
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
        MenuDesplegable.configurarMenu(this)

    }
}
