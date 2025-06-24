package equipo.dos.citasmedicas

import Persistencia.AdapterCita
import Persistencia.AdapterMedico
import Persistencia.fakebd
import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceActivity
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding
import java.util.Calendar

class frmPrincipalActivity : AppCompatActivity() {

    var adapter:AdapterCita? = null

    private val binding by lazy {
        ActivityFrmPrincipalBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frm_principal)
        cargarCitas()

        val toolbar = findViewById<Button>(R.id.btnMenu)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_miscitas)
        val nav = findViewById<NavigationView>(R.id.navegacion_menu)
        val btnAgendar: FloatingActionButton = findViewById(R.id.btnAgendar)

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
                R.id.btnOpcion -> {
                    var inte : Intent
                    if (sesion.tipoSesion() == "paciente"){
                        inte = Intent(this, frmAgendarActivity::class.java)
                    }else{
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
            true
        }

        val btnCalendario = findViewById<ImageButton>(R.id.btnCalendarioConsultaCitas)
        val tvFecha = findViewById<TextView>(R.id.tvConsultaFecha)

        btnCalendario.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val fechaSeleccionada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                tvFecha.text = fechaSeleccionada
            }, anio, mes, dia)

            datePicker.show()
        }

        btnAgendar.setOnClickListener(){
            var inte : Intent = Intent(this, frmAgendarActivity::class.java)
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(inte)
        }
    }
    fun cargarCitas() {
        adapter = AdapterCita(this, fakebd.citas, sesion.tipoSesion())
        var listaCitas: ListView = findViewById(R.id.lvCitas)
        listaCitas.adapter = adapter
    }
}
