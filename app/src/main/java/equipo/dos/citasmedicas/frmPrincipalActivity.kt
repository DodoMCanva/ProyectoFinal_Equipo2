package equipo.dos.citasmedicas

import Persistencia.medico
import Persistencia.paciente
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding

class frmPrincipalActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityFrmPrincipalBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_principal)

        var sesion = intent.getSerializableExtra("sesion")
        val tipoSesion : String = when (sesion){
            is paciente -> "paciente"
            is medico -> "medico"
            else -> "no asignado"
        }

        var drawerLayout = findViewById<DrawerLayout>(R.id.drawer_miscitas)
        var toolbar = findViewById<Button>(R.id.btnMenu)
        var nav = findViewById<NavigationView>(R.id.navegacion_menu)

        toolbar.setOnClickListener(View.OnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        })
        val hv = nav.getHeaderView(0)
        val btnCerrar = hv.find(R.id.btnMenuCerrarSesion)
        val btnOpcion = hv.findViewById<Button>(R.id.btnMenuOpcion)
        val btnMisCitas = hv.findViewById<Button>(R.id.btnMenuMisCitas)

        if (tipoSesion == "paciente"){
            btnOpcion.setText("")
            //btnOpcion.setIcon
            btnOpcion.setOnClickListener(){

            }
        }else{
            btnOpcion.setOnClickListener(){

            }
        }


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