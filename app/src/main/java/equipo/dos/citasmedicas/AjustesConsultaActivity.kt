package equipo.dos.citasmedicas

import Persistencia.medico
import Persistencia.paciente
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding

class AjustesConsultaActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityFrmPrincipalBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes_consulta)

        val sesion = intent.getSerializableExtra("sesion")
        val tipoSesion: String = when (sesion) {
            is paciente -> "paciente"
            is medico -> "medico"
            else -> "no asignado"
        }

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_ajustes_consultas)
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

        // Cambio de horario de consulta

        val ids = listOf(
            R.id.etLunesDeManana, R.id.etLunesAManana,
            R.id.etLunesDeTarde, R.id.etLunesATarde,
            R.id.etMartesDeManana, R.id.etMartesAManana,
            R.id.etMartesDeTarde, R.id.etMartesATarde,
            R.id.etMiercolesDeManana, R.id.etMiercolesAManana,
            R.id.etMiercolesDeTarde, R.id.etMiercolesATarde,
            R.id.etJuevesDeManana, R.id.etJuevesAManana,
            R.id.etJuevesDeTarde, R.id.etJuevesATarde,
            R.id.etViernesDeManana, R.id.etViernesAManana,
            R.id.etViernesDeTarde, R.id.etViernesATarde,
            R.id.etSabadoDeManana, R.id.etSabadoAManana,
            R.id.etSabadoDeTarde, R.id.etSabadoATarde,
            R.id.etDomingoDeManana, R.id.etDomingoAManana,
            R.id.etDomingoDeTarde, R.id.etDomingoATarde
        )

        for (id in ids) {
            val editText = findViewById<EditText>(id)
            editText.setOnClickListener {
                showTimePicker(editText)
            }
        }
        val btnGuardar = findViewById<Button>(R.id.btnGuardarConsulta)
        btnGuardar.setOnClickListener {
            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showTimePicker(editText: EditText) {
        val cal = java.util.Calendar.getInstance()
        val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = cal.get(java.util.Calendar.MINUTE)

        val picker = android.app.TimePickerDialog(this, { _, h, m ->
            val hora = String.format("%02d:%02d", h, m)
            editText.setText(hora)
        }, hour, minute, true)

        picker.show()
    }

}
