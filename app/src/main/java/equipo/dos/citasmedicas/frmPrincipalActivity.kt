package equipo.dos.citasmedicas

<<<<<<< Updated upstream
=======
import Persistencia.medico
import Persistencia.paciente
import android.content.Intent
>>>>>>> Stashed changes
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding

class frmPrincipalActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityFrmPrincipalBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_principal)



        var drawerLayout = findViewById<DrawerLayout>(R.id.drawer_miscitas)
        var toolbar = findViewById<Button>(R.id.btnMenu)
<<<<<<< Updated upstream
=======
        var nav = findViewById<NavigationView>(R.id.navegacion_menu)
>>>>>>> Stashed changes
        toolbar.setOnClickListener(View.OnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            //var miscitas = drawerLayout.findViewById<>()
        })
<<<<<<< Updated upstream
=======

        val menu = nav.menu
        val opcion = menu.findItem(R.id.btnMenuOpcion)

        if (tipoSesion == "paciente"){
            opcion.setIcon(R.drawable.date48)
            opcion.setTitle("Agendar")
        }else{
            opcion.setIcon(R.drawable.settings30)
            opcion.setTitle("Ajustes de Consulta")
        }
        nav.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnMenuMisCitas -> {
                    Toast.makeText(this, "Mis citas", Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.btnMenuOpcion -> {
                    if (tipoSesion == "paciente"){
                        intent = Intent(this, frmAgendarActivity::class.java)
                        startActivity(intent)
                    }else{
                        intent = Intent(this, AjustesConsultaActivity::class.java)
                        startActivity(intent)
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.btnMenuCerrarSesion -> {
                    Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                    // Redirigir a login, limpiar preferencias, etc.
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                else -> false
            }
        }
>>>>>>> Stashed changes




        val tipoUsuario = intent.getStringExtra("tipoUsuario") ?: "paciente"
        val lvMedicos = findViewById<ListView>(R.id.lvMedicos)

        if (tipoUsuario == "doctor") {
            val citasMedico = listOf(
                CitaMedico("21 de junio 2025", "10:00", "Luis Morales", "Chequeo general", "Completada"),
                CitaMedico("22 de junio 2025", "11:00", "Fernanda Pérez", "Dolor abdominal", "Confirmada")
            )

            val adapter = AdapterCitaMedico(this, citasMedico)
            lvMedicos.adapter = adapter
        } else {
            val citasPaciente = listOf(
                CitaPaciente("22 de junio 2025", "14:00", "Dermatología", "Dr. Ruiz"),
                CitaPaciente("23 de junio 2025", "09:00", "Ginecología", "Dra. Gómez")
            )

            val adapter = AdapterCitaPaciente(this, citasPaciente)
            lvMedicos.adapter = adapter
        }

    }



}