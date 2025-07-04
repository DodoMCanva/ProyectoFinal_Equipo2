package equipo.dos.citasmedicas.Fragmentos

import Persistencia.cita
import Persistencia.paciente // Asegúrate de importar la clase paciente
import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.ImageView // Importar ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.bumptech.glide.Glide // Importar Glide
import equipo.dos.citasmedicas.R
import equipo.dos.citasmedicas.frmPrincipalActivity


class DetalleCitaMedicoFragment : Fragment() {

    private lateinit var citaId: String
    private lateinit var imgFotoPacienteDetalle: ImageView // Variable para el ImageView de la foto del paciente

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detalle_cita_medico, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        citaId = arguments?.getString("citaId") ?: ""
        if (citaId.isEmpty()) {
            Toast.makeText(requireContext(), "Error: ID de cita no recibido.", Toast.LENGTH_SHORT).show()
            return
        }

        imgFotoPacienteDetalle = view.findViewById(R.id.imgFotoPerfil)
        cargarDatosDeCita(citaId)


        val finalizarCita = view.findViewById<Button>(R.id.btnFinalizarDetallesCitaMedico)
        finalizarCita.setOnClickListener {
            mostrarDialogSubirReceta()
        }
        val reprodetalleMedico = view.findViewById<Button>(R.id.btnReprogramarDetallesMedico)
        reprodetalleMedico.setOnClickListener {
            mostrarDialogReprogramarCita()
        }

        val cancelarDetallesMedico = view.findViewById<Button>(R.id.btnCancelarDetallesMedico)
        cancelarDetallesMedico.setOnClickListener {
            mostrarDialogDeCancelacion()
        }
    }

    private fun cargarDatosDeCita(citaId: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("usuarios/citas").child(citaId)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val citaData = snapshot.getValue(cita::class.java)

                if (citaData != null) {
                    view?.findViewById<TextView>(R.id.tvPacienteDetalleCitaMedico)?.text = citaData.nombrePaciente
                    view?.findViewById<TextView>(R.id.tvFechaDetalleCitaMedico)?.text = citaData.fecha
                    view?.findViewById<TextView>(R.id.tvHoraDetalleCitaMedico)?.text = citaData.hora
                    view?.findViewById<TextView>(R.id.tvEstadoDetalleCitaMedico)?.text = citaData.estado
                    view?.findViewById<TextView>(R.id.tvMotivoDetalleCitaMedico)?.text = citaData.motivo

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

                    // Lógica para cargar la imagen del PACIENTE
                    val idPaciente = citaData.idPaciente // Asume que tu objeto 'cita' tiene un campo 'idPaciente'
                    if (!idPaciente.isNullOrEmpty()) {
                        val refPaciente = FirebaseDatabase.getInstance().getReference("usuarios/pacientes/$idPaciente")
                        refPaciente.get().addOnSuccessListener { pacienteSnapshot ->
                            val pacienteData = pacienteSnapshot.getValue(paciente::class.java) // <--- Declaración corregida
                            pacienteData?.fotoPerfil?.let { fotoUrl ->
                                Glide.with(this@DetalleCitaMedicoFragment)
                                    .load(fotoUrl)
                                    .placeholder(R.drawable.usuario) // Imagen por defecto mientras carga
                                    .error(R.drawable.usuario)       // Imagen si hay error de carga
                                    .into(imgFotoPacienteDetalle)
                            } ?: run {
                                imgFotoPacienteDetalle.setImageResource(R.drawable.usuario) // Si no hay fotoPerfil
                            }
                        }.addOnFailureListener {
                            Log.e("Firebase", "Error al cargar datos del paciente: ${it.message}")
                            imgFotoPacienteDetalle.setImageResource(R.drawable.usuario) // Fallback en error de Firebase
                        }
                    } else {
                        imgFotoPacienteDetalle.setImageResource(R.drawable.usuario) // Si no hay ID de paciente en la cita
                    }
                } else {
                    Toast.makeText(requireContext(), "Cita no encontrada.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al cargar cita: ${error.message}", Toast.LENGTH_SHORT).show()
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

    @RequiresApi(Build.VERSION_CODES.N)
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

        fun generarHorasCada30Min(): List<String> { /* Implementa tu lógica de generación de horas */ return mutableListOf() }
        val adapterHoras = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, generarHorasCada30Min())
        adapterHoras.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Mejor estilo para el spinner
        spHoraDialog.adapter = adapterHoras
        spHoraDialog.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: android.widget.AdapterView<*>?, p1: View?, p2: Int, p3: Long) { tvHoraSeleccionadaDialog.text = p0?.getItemAtPosition(p2).toString() }
            override fun onNothingSelected(p0: android.widget.AdapterView<*>?) {}
        }

        btnCalendarioDialog.setOnClickListener { // Agregado el listener para el calendario
            val calendario = Calendar.getInstance()
            val datePicker = DatePickerDialog(requireContext(),
                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    val fechaSeleccionada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    tvFechaDialog.text = fechaSeleccionada
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }


        btnCancelarReprogramacion?.setOnClickListener { dialog.dismiss() }
        btnCompletarReprogramacionDialog?.setOnClickListener {
            val nuevaFecha = tvFechaDialog.text.toString()
            val nuevaHora = tvHoraSeleccionadaDialog.text.toString()
            if (nuevaFecha.isEmpty() || nuevaHora.isEmpty()) {
                Toast.makeText(requireContext(), "Fecha y hora son obligatorias para reprogramar.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
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
            actualizarcancelado(citaId)
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
    override fun onResume() {
        super.onResume()
        val tvEncabezado: TextView? = (activity as? frmPrincipalActivity)?.findViewById(R.id.encabezadoPrincipal)
        tvEncabezado?.text = "Detalle Cita"
    }
}