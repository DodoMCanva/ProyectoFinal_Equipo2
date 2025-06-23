package equipo.dos.citasmedicas

import Persistencia.medico
import Persistencia.paciente
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding
import java.util.Calendar

class frmAgendarMedicoActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityFrmPrincipalBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frm_agendar_medico)

        //calendario
        val btnCalendario = findViewById<ImageButton>(R.id.btnCalendario)
        val tvFecha = findViewById<TextView>(R.id.tvAgendarFecha)

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

        //spinner hora
        val spHora = findViewById<Spinner>(R.id.spHora)
        val horasDisponibles = listOf("09:00 AM", "10:00 AM", "11:00 AM", "01:00 PM", "03:00 PM", "04:30 PM")

        val adapterHoras = ArrayAdapter(this, android.R.layout.simple_spinner_item, horasDisponibles)
        adapterHoras.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spHora.adapter = adapterHoras

        //btnConfirmar

        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        val txtMotivo = findViewById<EditText>(R.id.txtMotivo)

        btnConfirmar.setOnClickListener {
            val fecha = tvFecha.text.toString()
            val hora = spHora.selectedItem.toString()
            val motivo = txtMotivo.text.toString()

            if (motivo.isEmpty()) {
                Toast.makeText(this, "Escribe un motivo para la cita", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, frmPrincipalActivity::class.java)//aqui cambiar al layout de confirmacion
            intent.putExtra("fecha", fecha)
            intent.putExtra("hora", hora)
            intent.putExtra("motivo", motivo)
            startActivity(intent)
        }



        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        btnCancelar.setOnClickListener{
            intent = Intent(this, frmAgendarActivity::class.java)
            startActivity(intent)
        }



        //menu desplegable
        val medico = intent.getSerializableExtra("medico") as? medico

        if (medico != null) {
            val tvNombre = findViewById<TextView>(R.id.tvAgendarNombre)
            val tvMonto = findViewById<TextView>(R.id.tvMonto)

            tvNombre.text = medico.nombre
            tvMonto.text = "$${medico.costoConsulta}"
        } else {
            Toast.makeText(this, "Médico no recibido", Toast.LENGTH_SHORT).show()
            finish()
        }

        val sesion = intent.getSerializableExtra("sesion")
        val tipoSesion: String = when (sesion) {
            is paciente -> "paciente"
            is medico -> "medico"
            else -> "no asignado"
        }

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_agendar_medico)
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
