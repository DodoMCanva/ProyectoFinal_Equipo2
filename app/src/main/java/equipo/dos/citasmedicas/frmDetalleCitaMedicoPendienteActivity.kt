package equipo.dos.citasmedicas

import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import equipo.dos.citasmedicas.databinding.ActivityFrmDetalleCitaMedicoPendienteBinding
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding
import java.util.Calendar

class frmDetalleCitaMedicoPendienteActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityFrmDetalleCitaMedicoPendienteBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)


        val campoNombre: TextView = findViewById(R.id.tvPacienteDetalleCitaMedico)
        val campoEdad: TextView = findViewById(R.id.tvEdadDetalleCitaMedico)
        val campoGenero: TextView = findViewById(R.id.tvGeneroDetalleCitaMedico)
        val campoTelefono: TextView = findViewById(R.id.tvTelefonoDetalleCitaMedico)
        val campoFecha: TextView = findViewById(R.id.tvFechaDetalleCitaMedico)
        val campoHora: TextView = findViewById(R.id.tvHoraDetalleCitaMedico)
        val campoEstado: TextView = findViewById(R.id.tvEstadoDetalleCitaMedico)
        val campoMotivo: TextView = findViewById(R.id.tvMotivoDetalleCitaMedico)

        val seccionReceta: LinearLayout = findViewById(R.id.llSeccionRecteaDetalleCita)
        val seccionBotones: LinearLayout = findViewById(R.id.llSeccionOpcionesDetallesCita)

        val imgFotoPerfil: ImageView = findViewById(R.id.imgFotoPerfil)

        var estado = intent.getStringExtra("estado")
        when (estado) {

            "Pendiente" -> {
                seccionReceta.visibility = View.INVISIBLE
            }

            "Completada" -> {
                seccionBotones.visibility = View.INVISIBLE
            }

            "Cancelada" -> {
                seccionBotones.visibility = View.INVISIBLE
                seccionReceta.visibility = View.INVISIBLE
            }

            else -> Toast.makeText(this, "Se mando", Toast.LENGTH_SHORT).show()
        }

        campoNombre.setText(intent.getStringExtra("nombre"))
        campoEdad.setText(intent.getStringExtra("edad"))
        campoGenero.setText(intent.getStringExtra("genero"))
        campoTelefono.setText(intent.getStringExtra("telefono"))
        campoFecha.setText(intent.getStringExtra("fecha"))
        campoHora.setText(intent.getStringExtra("hora"))
        campoEstado.setText(intent.getStringExtra("estado"))
        campoMotivo.setText(intent.getStringExtra("motivo"))



        val drawerLayout = binding.drawerDetalleCitaMedico
        val toolbar =
            binding.btnMenu
        val nav =
            binding.navegacionMenu

        toolbar.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val menu = nav.menu
        val opcion = menu.findItem(R.id.btnMenuAjusteConsulta)


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

        // cargar imagen de perfil
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


        btnPerfil.setOnClickListener {
            val inte = Intent(this, frmMiPerfilActivity::class.java)
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(inte)
        }

        btnMenuCerrar.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        binding.btnFinalizarDetallesCitaMedico.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_subir_receta)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
            dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

            val btnCancelarReceta = dialog.findViewById<Button>(R.id.btnCancelarForm)
            btnCancelarReceta.setOnClickListener {
                dialog.dismiss()
            }
            val btnCompletar = dialog.findViewById<Button>(R.id.btnCompletar)
            btnCompletar.setOnClickListener {

                seccionReceta.visibility = View.VISIBLE

                seccionBotones.visibility = View.GONE
                dialog.dismiss()
                actualizarCompletada()
            }
            dialog.show()
        }

        binding.btnReprogramarDetallesMedico.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_reprogramar_cita)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
            dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

            val btnCalendarioDialog =
                dialog.findViewById<ImageButton>(R.id.btnCalendarioReprogramar)
            val tvFechaDialog =
                dialog.findViewById<TextView>(R.id.tvRepFecha)

            if (btnCalendarioDialog == null || tvFechaDialog == null) {
                Toast.makeText(
                    this,
                    "Error: Vistas de fecha no encontradas en diálogo.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            btnCalendarioDialog.setOnClickListener {
                val calendario = Calendar.getInstance()
                val anio = calendario.get(Calendar.YEAR)
                val mes = calendario.get(Calendar.MONTH)
                val dia = calendario.get(Calendar.DAY_OF_MONTH)

                val datePicker = DatePickerDialog(
                    this,
                    { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                        val fechaSeleccionada =
                            String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                        tvFechaDialog.text = fechaSeleccionada
                    },
                    anio,
                    mes,
                    dia
                )
                datePicker.show()
            }

            val spHoraDialog = dialog.findViewById<Spinner>(R.id.spHoraReprogramarCita)
            val tvHoraSeleccionadaDialog =
                dialog.findViewById<TextView>(R.id.tvHoraReprogramarCita) // TextView para la HORA

            if (spHoraDialog == null || tvHoraSeleccionadaDialog == null) {
                Toast.makeText(
                    this,
                    "Error: Vistas de hora no encontradas en diálogo.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            fun generarHorasCada30Min(): List<String> {
                val horas = mutableListOf<String>()
                var hora = 7
                var minuto = 0
                while (hora < 19 || (hora == 19 && minuto == 0)) {
                    val amPm = if (hora < 12) "AM" else "PM"
                    val hora12 = when {
                        hora == 0 -> 12
                        hora == 12 -> 12
                        hora > 12 -> hora - 12
                        else -> hora
                    }
                    val horaFormateada = String.format("%d:%02d %s", hora12, minuto, amPm)
                    horas.add(horaFormateada)
                    minuto += 30
                    if (minuto >= 60) {
                        minuto = 0
                        hora++
                    }
                }
                return horas
            }

            val horasDisponibles = generarHorasCada30Min()
            val adapterHoras =
                ArrayAdapter(this, android.R.layout.simple_spinner_item, horasDisponibles)
            adapterHoras.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spHoraDialog.adapter = adapterHoras

            spHoraDialog.onItemSelectedListener =
                object : android.widget.AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: android.widget.AdapterView<*>,
                        view: android.view.View?,
                        position: Int,
                        id: Long
                    ) {
                        val horaSeleccionada = parent.getItemAtPosition(position).toString()
                        tvHoraSeleccionadaDialog.text =
                            horaSeleccionada
                    }

                    override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                    }
                }
            val btnCancelarReprogramacion = dialog.findViewById<Button>(R.id.btnCancelarRep)
            btnCancelarReprogramacion?.setOnClickListener {
                dialog.dismiss()
            }



            val btnCompletarReprogramacionDialog = dialog.findViewById<Button>(R.id.btnCompletarRep)
            btnCompletarReprogramacionDialog?.setOnClickListener {
                val nuevaFecha = tvFechaDialog.text.toString()
                val nuevaHora = tvHoraSeleccionadaDialog.text.toString()
                Toast.makeText(
                    this,
                    "Cita reprogramada para: $nuevaFecha a las $nuevaHora",
                    Toast.LENGTH_LONG
                ).show()

                actualizarReprogramar()
                dialog.dismiss()
            }


            dialog.show()
        }


        binding.btnCancelarDetallesMedico.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_cancelar_cita)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
            dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

            val btnAtras = dialog.findViewById<Button>(R.id.btnAtrasCancelacion)
            btnAtras.setOnClickListener {
                dialog.dismiss()
            }
            val btnConfirmar = dialog.findViewById<Button>(R.id.btnConfirmarCancelacion)
            btnConfirmar.setOnClickListener {
                dialog.dismiss()
                actualizarcancelado()
            }



            dialog.show()
        }

    }

    private fun actualizarcancelado() {
        val campoEstado: TextView = findViewById(R.id.tvEstadoDetalleCitaMedico)
        val seccionBotones: LinearLayout = findViewById(R.id.llSeccionOpcionesDetallesCita)
        val seccionReceta: LinearLayout = findViewById(R.id.llSeccionRecteaDetalleCita)

        campoEstado.text = "Cancelada"
        seccionBotones.visibility = View.GONE
        seccionReceta.visibility = View.GONE

        Toast.makeText(this, "Cita cancelada", Toast.LENGTH_SHORT).show()

    }

    private fun actualizarCompletada() {
        val campoEstado: TextView = findViewById(R.id.tvEstadoDetalleCitaMedico)
        campoEstado.text = "Completada"
        Toast.makeText(this, "Cita completada y receta lista!", Toast.LENGTH_SHORT).show()
    }

    private fun actualizarReprogramar() {
        val campoEstado: TextView = findViewById(R.id.tvEstadoDetalleCitaMedico)
        campoEstado.text = "Pendiente"

    }
}
