package equipo.dos.citasmedicas.Fragmentos

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import equipo.dos.citasmedicas.R
import equipo.dos.citasmedicas.helpers.MenuDesplegable
import equipo.dos.citasmedicas.databinding.ActivityFrmDetalleCitaMedicoPendienteBinding


class DetalleCitaMedicoFragment : Fragment() {

    private lateinit var citaId: String
    private val binding by lazy {
        ActivityFrmDetalleCitaMedicoPendienteBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detalle_cita_medico, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

     //   citaId = intent.getStringExtra("citaId") ?: ""
        if (citaId.isEmpty()) {
            Toast.makeText(requireContext(), "Error: ID de cita no recibido.", Toast.LENGTH_SHORT).show()
           // finish()
            return
        }

        cargarDatosDeCita(citaId)


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
                    view?.findViewById<TextView>(R.id.tvPacienteDetalleCitaMedico)?.text = citaData.nombrePaciente
                    view?.findViewById<TextView>(R.id.tvFechaDetalleCitaMedico)?.text = citaData.fecha
                    view?.findViewById<TextView>(R.id.tvHoraDetalleCitaMedico)?.text = citaData.hora
                    view?.findViewById<TextView>(R.id.tvEstadoDetalleCitaMedico)?.text = citaData.estado
                    view?.findViewById<TextView>(R.id.tvMotivoDetalleCitaMedico)?.text = citaData.motivo

                    // Muestra u oculta secciones según el estado de la cita
                    val seccionReceta = view?.findViewById<LinearLayout>(R.id.llSeccionRecteaDetalleCita)
                    val seccionBotones = view?.findViewById<LinearLayout>(R.id.llSeccionOpcionesDetallesCita)
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
                                view?.findViewById<TextView>(R.id.tvGeneroDetalleCitaMedico)?.text = paciente.genero
                                view?.findViewById<TextView>(R.id.tvTelefonoDetalleCitaMedico)?.text = paciente.telefono

                                // Calcular edad si es posible (requiere Android 8.0+)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    val edad = paciente.calcularEdad()
                                    view?.findViewById<TextView>(R.id.tvEdadDetalleCitaMedico)?.text = edad.toString()
                                } else {
                                    view?.findViewById<TextView>(R.id.tvEdadDetalleCitaMedico)?.text = "N/D"
                                }
                            }

                        }
                    }

                } else {
                   // Toast.makeText(requireContext()@frmDetalleCitaMedicoPendienteActivity, "Cita no encontrada.", Toast.LENGTH_SHORT).show()
                    //finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
              //  Toast.makeText(requireContext()@frmDetalleCitaMedicoPendienteActivity, "Error al cargar cita: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarDialogSubirReceta() {
        val dialog = Dialog(requireContext())
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
        val dialog = Dialog(requireContext())
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
        val adapterHoras = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, generarHorasCada30Min())
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
        val dialog = Dialog(requireContext())
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
                Toast.makeText(requireContext(), "Cita cancelada con éxito en la base de datos.", Toast.LENGTH_SHORT).show()
                cargarDatosDeCita(citaId)
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error al cancelar cita: ${it.message}")
                Toast.makeText(requireContext(), "Error al cancelar la cita.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarCompletada(citaId: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("citas").child(citaId)
        val actualizacion = mapOf("estado" to "Completada")

        databaseRef.updateChildren(actualizacion)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Cita completada y receta lista!", Toast.LENGTH_SHORT).show()
                cargarDatosDeCita(citaId)
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error al completar cita: ${it.message}")
                Toast.makeText(requireContext(), "Error al completar la cita.", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), "Cita reprogramada con éxito.", Toast.LENGTH_SHORT).show()
                cargarDatosDeCita(citaId)
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error al reprogramar cita: ${it.message}")
                Toast.makeText(requireContext(), "Error al reprogramar la cita.", Toast.LENGTH_SHORT).show()
            }
    }




}