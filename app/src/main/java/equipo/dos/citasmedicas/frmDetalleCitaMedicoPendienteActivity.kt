package equipo.dos.citasmedicas

import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.util.Log
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
import equipo.dos.citasmedicas.helpers.MenuDesplegable
import java.util.Calendar

class frmDetalleCitaMedicoPendienteActivity : AppCompatActivity() {
    private lateinit var citaId: String
    private val binding by lazy {
        ActivityFrmDetalleCitaMedicoPendienteBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        citaId = intent.getStringExtra("citaId") ?: ""
        if (citaId.isEmpty()) {
            Toast.makeText(this, "Error: ID de cita no recibido.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDatosDeCita(citaId)

        MenuDesplegable.configurarMenu(this)

        binding.btnFinalizarDetallesCitaMedico.setOnClickListener {
            mostrarDialogSubirReceta()
        }

        binding.btnReprogramarDetallesMedico.setOnClickListener {
            mostrarDialogReprogramarCita()
        }

        binding.btnCancelarDetallesMedico.setOnClickListener {
            mostrarDialogDeCancelacion()
        }
    }

    private fun cargarDatosDeCita(citaId: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("usuarios/citas").child(citaId)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val citaData = snapshot.getValue(Persistencia.cita::class.java)

                if (citaData != null) {
                    findViewById<TextView>(R.id.tvPacienteDetalleCitaMedico).text = citaData.nombrePaciente
                    findViewById<TextView>(R.id.tvFechaDetalleCitaMedico).text = citaData.fecha
                    findViewById<TextView>(R.id.tvHoraDetalleCitaMedico).text = citaData.hora
                    findViewById<TextView>(R.id.tvEstadoDetalleCitaMedico).text = citaData.estado
                    findViewById<TextView>(R.id.tvMotivoDetalleCitaMedico).text = citaData.motivo

                    // Muestra u oculta secciones según el estado de la cita
                    val seccionReceta = findViewById<LinearLayout>(R.id.llSeccionRecteaDetalleCita)
                    val seccionBotones = findViewById<LinearLayout>(R.id.llSeccionOpcionesDetallesCita)
                    when (citaData.estado) {
                        "Pendiente" -> {
                            seccionReceta?.visibility = View.GONE
                            seccionBotones?.visibility = View.VISIBLE
                        }
                        "Completada" -> {
                            seccionReceta?.visibility = View.VISIBLE
                            seccionBotones?.visibility = View.GONE
                        }
                        "Cancelada" -> {
                            seccionReceta?.visibility = View.GONE
                            seccionBotones?.visibility = View.GONE
                        }
                    }

                    // Cargar los datos del paciente
                    val idPaciente = citaData.idPaciente
                    if (!idPaciente.isNullOrEmpty()) {
                        val refPaciente = FirebaseDatabase.getInstance().getReference("usuarios/pacientes/$idPaciente")
                        refPaciente.get().addOnSuccessListener { snapshotPaciente ->
                            val paciente = snapshotPaciente.getValue(Persistencia.paciente::class.java)
                            if (paciente != null) {
                                findViewById<TextView>(R.id.tvGeneroDetalleCitaMedico).text = paciente.genero
                                findViewById<TextView>(R.id.tvTelefonoDetalleCitaMedico).text = paciente.telefono

                                // Calcular edad si es posible (requiere Android 8.0+)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    val edad = paciente.calcularEdad()
                                    findViewById<TextView>(R.id.tvEdadDetalleCitaMedico).text = edad.toString()
                                } else {
                                    findViewById<TextView>(R.id.tvEdadDetalleCitaMedico).text = "N/D"
                                }
                            }

                        }
                    }

                } else {
                    Toast.makeText(this@frmDetalleCitaMedicoPendienteActivity, "Cita no encontrada.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@frmDetalleCitaMedicoPendienteActivity, "Error al cargar cita: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarDialogSubirReceta() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_subir_receta)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout((resources.displayMetrics.widthPixels * 0.90).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

        val btnCancelarReceta = dialog.findViewById<Button>(R.id.btnCancelarForm)
        val btnCompletar = dialog.findViewById<Button>(R.id.btnCompletar)

        btnCancelarReceta.setOnClickListener { dialog.dismiss() }
        btnCompletar.setOnClickListener {
            dialog.dismiss()
            actualizarCompletada(citaId)
        }
        dialog.show()
    }

    private fun mostrarDialogReprogramarCita() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_reprogramar_cita)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout((resources.displayMetrics.widthPixels * 0.90).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

        // Aquí van tus vistas del diálogo
        val btnCalendarioDialog = dialog.findViewById<ImageButton>(R.id.btnCalendarioReprogramar)
        val tvFechaDialog = dialog.findViewById<TextView>(R.id.tvRepFecha)
        val spHoraDialog = dialog.findViewById<Spinner>(R.id.spHoraReprogramarCita)
        val tvHoraSeleccionadaDialog = dialog.findViewById<TextView>(R.id.tvHoraReprogramarCita)
        val btnCompletarReprogramacionDialog = dialog.findViewById<Button>(R.id.btnCompletarRep)
        val btnCancelarReprogramacion = dialog.findViewById<Button>(R.id.btnCancelarRep)

        fun generarHorasCada30Min(): List<String> { /* ... */ return mutableListOf() }
        val adapterHoras = ArrayAdapter(this, android.R.layout.simple_spinner_item, generarHorasCada30Min())
        spHoraDialog.adapter = adapterHoras
        spHoraDialog.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener { /* ... */ override fun onItemSelected(p0: android.widget.AdapterView<*>?, p1: View?, p2: Int, p3: Long) { tvHoraSeleccionadaDialog.text = p0?.getItemAtPosition(p2).toString() } override fun onNothingSelected(p0: android.widget.AdapterView<*>?) {} }

        btnCancelarReprogramacion?.setOnClickListener { dialog.dismiss() }
        btnCompletarReprogramacionDialog?.setOnClickListener {
            val nuevaFecha = tvFechaDialog.text.toString()
            val nuevaHora = tvHoraSeleccionadaDialog.text.toString()
            actualizarReprogramar(citaId, nuevaFecha, nuevaHora)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun mostrarDialogDeCancelacion() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_cancelar_cita)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout((resources.displayMetrics.widthPixels * 0.90).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

        val btnConfirmar = dialog.findViewById<Button>(R.id.btnConfirmarCancelacion)
        val btnAtras = dialog.findViewById<Button>(R.id.btnAtrasCancelacion)

        btnAtras.setOnClickListener { dialog.dismiss() }
        btnConfirmar.setOnClickListener {
            dialog.dismiss()
            actualizarcancelado(citaId) // Llama a la función de actualización con el ID
        }
        dialog.show()
    }
    private fun actualizarcancelado(citaId: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("citas").child(citaId)
        val actualizacion = mapOf("estado" to "Cancelada")

        databaseRef.updateChildren(actualizacion)
            .addOnSuccessListener {
                Toast.makeText(this, "Cita cancelada con éxito en la base de datos.", Toast.LENGTH_SHORT).show()
                cargarDatosDeCita(citaId)
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error al cancelar cita: ${it.message}")
                Toast.makeText(this, "Error al cancelar la cita.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarCompletada(citaId: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("citas").child(citaId)
        val actualizacion = mapOf("estado" to "Completada")

        databaseRef.updateChildren(actualizacion)
            .addOnSuccessListener {
                Toast.makeText(this, "Cita completada y receta lista!", Toast.LENGTH_SHORT).show()
                cargarDatosDeCita(citaId)
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error al completar cita: ${it.message}")
                Toast.makeText(this, "Error al completar la cita.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarReprogramar(citaId: String, nuevaFecha: String, nuevaHora: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("citas").child(citaId)
        val actualizacion = mapOf(
            "estado" to "Pendiente",
            "fecha" to nuevaFecha,
            "hora" to nuevaHora
        )

        databaseRef.updateChildren(actualizacion)
            .addOnSuccessListener {
                Toast.makeText(this, "Cita reprogramada con éxito.", Toast.LENGTH_SHORT).show()
                cargarDatosDeCita(citaId)
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error al reprogramar cita: ${it.message}")
                Toast.makeText(this, "Error al reprogramar la cita.", Toast.LENGTH_SHORT).show()
            }
    }
}
