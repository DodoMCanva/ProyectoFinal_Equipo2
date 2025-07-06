package equipo.dos.citasmedicas.Fragmentos

import Persistencia.cita
import Persistencia.paciente
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
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
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.commit
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import equipo.dos.citasmedicas.R
import equipo.dos.citasmedicas.frmPrincipalActivity
import java.util.Locale


val UPLOAD_PRESET_CITAS_MEDICAS = "Citas Medicas"

@RequiresApi(Build.VERSION_CODES.O)
class DetalleCitaMedicoFragment : Fragment() {

    private lateinit var citaId: String
    private var imgFotoPacienteDetalle: ImageView? = null
    private var tvNotasReceta: TextView? = null
    private var imagenRecetaUri: Uri? = null

    private val pickImageRecipe =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    imagenRecetaUri = uri
                    val dialogImageView =
                        currentRecipeDialog?.findViewById<ImageView>(R.id.ivPreviewReceta)
                    dialogImageView?.setImageURI(uri)
                    Log.d(
                        "RecetaUpload",
                        "Imagen de receta seleccionada para previsualización en diálogo: $uri"
                    )
                }
            }
        }
    private var currentRecipeDialog: Dialog? = null


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
            Toast.makeText(requireContext(), "Error: ID de cita no recibido.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        imgFotoPacienteDetalle = view.findViewById(R.id.imgFotoPerfil)
        tvNotasReceta = view.findViewById(R.id.tvNotasRecetaDetalleCita)


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
        val databaseRef =
            FirebaseDatabase.getInstance().getReference("usuarios/citas").child(citaId)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val citaData = snapshot.getValue(cita::class.java)

                if (citaData != null) {
                    view?.findViewById<TextView>(R.id.tvPacienteDetalleCitaMedico)?.text =
                        citaData.nombrePaciente
                    view?.findViewById<TextView>(R.id.tvFechaDetalleCitaMedico)?.text =
                        citaData.fecha
                    view?.findViewById<TextView>(R.id.tvHoraDetalleCitaMedico)?.text = citaData.hora
                    view?.findViewById<TextView>(R.id.tvEstadoDetalleCitaMedico)?.text =
                        citaData.estado
                    view?.findViewById<TextView>(R.id.tvMotivoDetalleCitaMedico)?.text =
                        citaData.motivo


                    val seccionRecetaLinearLayout =
                        view?.findViewById<LinearLayout>(R.id.llSeccionRecteaDetalleCita)
                    val seccionBotones =
                        view?.findViewById<LinearLayout>(R.id.llSeccionOpcionesDetallesCita)

                    if (!citaData.notas.isNullOrEmpty()) {
                        tvNotasReceta?.text = citaData.notas
                        tvNotasReceta?.visibility = View.VISIBLE
                        view?.findViewById<TextView>(R.id.tvNotasRecetaDetalleCita)?.visibility =
                            View.VISIBLE
                    } else {
                        tvNotasReceta?.visibility = View.GONE
                        view?.findViewById<TextView>(R.id.tvNotasRecetaDetalleCita)?.visibility =
                            View.GONE
                    }

                    when (citaData.estado) {
                        "Pendiente" -> {
                            seccionRecetaLinearLayout?.visibility = View.GONE
                            seccionBotones?.visibility = View.VISIBLE
                            view?.findViewById<Button>(R.id.btnFinalizarDetallesCitaMedico)?.isEnabled =
                                true
                            view?.findViewById<Button>(R.id.btnReprogramarDetallesMedico)?.isEnabled =
                                true
                            view?.findViewById<Button>(R.id.btnCancelarDetallesMedico)?.isEnabled =
                                true
                        }

                        "Completada" -> {
                            seccionRecetaLinearLayout?.visibility = View.VISIBLE
                            seccionBotones?.visibility = View.GONE
                        }

                        "Cancelada" -> {
                            seccionRecetaLinearLayout?.visibility = View.GONE
                            seccionBotones?.visibility = View.GONE
                            view?.findViewById<Button>(R.id.btnFinalizarDetallesCitaMedico)?.isEnabled =
                                false
                            view?.findViewById<Button>(R.id.btnReprogramarDetallesMedico)?.isEnabled =
                                false
                            view?.findViewById<Button>(R.id.btnCancelarDetallesMedico)?.isEnabled =
                                false
                        }
                    }

                    val idPaciente =
                        citaData.idPaciente
                    if (!idPaciente.isNullOrEmpty()) {
                        val refPaciente = FirebaseDatabase.getInstance()
                            .getReference("usuarios/pacientes/$idPaciente")
                        refPaciente.get().addOnSuccessListener { pacienteSnapshot ->
                            val pacienteData =
                                pacienteSnapshot.getValue(paciente::class.java)
                            pacienteData?.fotoPerfil?.let { fotoUrl ->
                                Glide.with(this@DetalleCitaMedicoFragment)
                                    .load(fotoUrl)
                                    .placeholder(R.drawable.usuario)
                                    .error(R.drawable.usuario)
                                    .into(imgFotoPacienteDetalle!!)
                            } ?: run {
                                imgFotoPacienteDetalle!!.setImageResource(R.drawable.usuario)
                            }
                        }.addOnFailureListener {
                            Log.e("Firebase", "Error al cargar datos del paciente: ${it.message}")
                            imgFotoPacienteDetalle!!.setImageResource(R.drawable.usuario)
                        }
                    } else {
                        imgFotoPacienteDetalle!!.setImageResource(R.drawable.usuario)
                    }
                } else {
                    Toast.makeText(requireContext(), "Cita no encontrada.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Error al cargar cita: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun mostrarDialogSubirReceta() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_subir_receta)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        currentRecipeDialog = dialog


        val btnCancelarReceta = dialog.findViewById<Button>(R.id.btnCancelarForm)
        val btnCompletar = dialog.findViewById<Button>(R.id.btnCompletar)
        val btnBuscarImagen = dialog.findViewById<Button>(R.id.btnBuscarImagen)
        val etNotas = dialog.findViewById<EditText>(R.id.etNotas)
        val ivPreviewReceta = dialog.findViewById<ImageView>(R.id.ivPreviewReceta)

        imagenRecetaUri = null
        ivPreviewReceta.setImageDrawable(null)


        val databaseRef =
            FirebaseDatabase.getInstance().getReference("usuarios/citas").child(citaId)
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val citaData = snapshot.getValue(cita::class.java)
                citaData?.notas?.let {
                    etNotas.setText(it)
                }
                citaData?.urlReceta?.let { url ->
                    if (url.startsWith("http")) {
                        Glide.with(dialog.context)
                            .load(url)
                            .placeholder(R.drawable.receta_detalle_cita_medico)
                            .error(R.drawable.receta_detalle_cita_medico)
                            .into(ivPreviewReceta)
                        ivPreviewReceta.tag = url
                        Log.d("RecetaDialog", "Cargando receta existente en diálogo: $url")
                    } else {
                        ivPreviewReceta.setImageDrawable(null)
                        ivPreviewReceta.tag = null
                        Log.d(
                            "RecetaDialog",
                            "Receta existente no es URL válida o está vacía."
                        )
                    }
                } ?: run {
                    ivPreviewReceta.setImageDrawable(null)
                    ivPreviewReceta.tag = null
                    Log.d("RecetaDialog", "No hay URL de receta existente.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DetalleCita", "Error al cargar datos para dialog: ${error.message}")
            }
        })


        btnBuscarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageRecipe.launch(intent)
        }
        btnCancelarReceta.setOnClickListener {
            imagenRecetaUri = null
            dialog.dismiss()
        }
        btnCompletar.setOnClickListener {
            val notas = etNotas.text.toString()

            if (imagenRecetaUri != null) {
                subirImagenARecetaCloudinary(imagenRecetaUri!!) { imageUrl ->
                    actualizarCompletada(citaId, imageUrl, notas)
                    dialog.dismiss()
                    imagenRecetaUri = null
                }
            } else {
                val existingImageUrl = ivPreviewReceta.tag as? String
                actualizarCompletada(citaId, existingImageUrl, notas)
                dialog.dismiss()
            }

            val fragment = CitasFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragmento, fragment)
                .addToBackStack(null)
                .commit()

        }
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun mostrarDialogReprogramarCita() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_reprogramar_cita)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val btnCalendarioDialog = dialog.findViewById<ImageButton>(R.id.btnCalendarioReprogramar)
        val tvFechaDialog = dialog.findViewById<TextView>(R.id.tvRepFecha)
        val spHoraDialog = dialog.findViewById<Spinner>(R.id.spHoraReprogramarCita)
        val tvHoraSeleccionadaDialog = dialog.findViewById<TextView>(R.id.tvHoraReprogramarCita)
        val btnCompletarReprogramacionDialog = dialog.findViewById<Button>(R.id.btnCompletarRep)
        val btnCancelarReprogramacion = dialog.findViewById<Button>(R.id.btnCancelarRep)

        fun generarHorasCada30Min(): List<String> {
            val horas = mutableListOf<String>()
            var hora = 7
            var minuto = 0

            while (hora < 19 || (hora == 19 && minuto == 0)) {
                val amPm = if (hora < 12) "AM" else "PM"
                val hora12 = if (hora % 12 == 0) 12 else hora % 12
                val horaFormateada = String.format(Locale.getDefault(), "%d:%02d %s", hora12, minuto, amPm)
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
        val adapterHoras = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, horasDisponibles)
        adapterHoras.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spHoraDialog.adapter = adapterHoras
        spHoraDialog.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: android.widget.AdapterView<*>?,
                    p1: View?,
                    p2: Int,
                    p3: Long
                ) {
                    tvHoraSeleccionadaDialog.text = p0?.getItemAtPosition(p2).toString()
                }

                override fun onNothingSelected(p0: android.widget.AdapterView<*>?) {}
            }
        btnCalendarioDialog.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                requireContext(),
                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    val fechaSeleccionada =
                        String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    tvFechaDialog.text = fechaSeleccionada
                },
                anio, mes, dia
            )

            datePicker.datePicker.minDate = Calendar.getInstance().timeInMillis
            val maxDateCalendar = Calendar.getInstance()
            maxDateCalendar.add(Calendar.YEAR, 5)
            datePicker.datePicker.maxDate = maxDateCalendar.timeInMillis

            datePicker.show()
        }
        FirebaseDatabase.getInstance().getReference("usuarios/citas").child(citaId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val citaData = snapshot.getValue(cita::class.java)
                    if (citaData != null) {
                        tvFechaDialog.text = citaData.fecha
                        tvHoraSeleccionadaDialog.text = citaData.hora
                        val horaIndex = horasDisponibles.indexOf(citaData.hora)
                        if (horaIndex != -1) {
                            spHoraDialog.setSelection(horaIndex)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })


        btnCancelarReprogramacion?.setOnClickListener { dialog.dismiss() }
        btnCompletarReprogramacionDialog?.setOnClickListener {
            val nuevaFecha = tvFechaDialog.text.toString()
            val nuevaHora = tvHoraSeleccionadaDialog.text.toString()
            if (nuevaFecha.isEmpty() || nuevaHora.isEmpty() || nuevaFecha == "29/05/2025") {
                Toast.makeText(requireContext(), "Fecha y hora son obligatorias para reprogramar.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            actualizarReprogramar(citaId, nuevaFecha, nuevaHora)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun generarHorasCada30Min(): List<String> {
        val horas = mutableListOf<String>()
        var hora = 7
        var minuto = 0

        while (hora < 19 || (hora == 19 && minuto == 0)) {
            val amPm = if (hora < 12) "AM" else "PM"
            val hora12 = if (hora % 12 == 0) 12 else hora % 12
            val horaFormateada = String.format(Locale.getDefault(), "%d:%02d %s", hora12, minuto, amPm)
            horas.add(horaFormateada)

            minuto += 30
            if (minuto >= 60) {
                minuto = 0
                hora++
            }
        }
        return horas
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
        val databaseRef = FirebaseDatabase.getInstance().getReference("usuarios/citas").child(citaId)
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

    private fun subirImagenARecetaCloudinary(uri: Uri, onComplete: (String?) -> Unit) {
        Toast.makeText(requireContext(), "Subiendo receta...", Toast.LENGTH_SHORT).show()
        MediaManager.get().upload(uri)
            .unsigned(UPLOAD_PRESET_CITAS_MEDICAS)
            .option("folder", "recetas")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    Log.d("CloudinaryUpload", "Upload receta start for $requestId")
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    Log.d("CloudinaryUpload", "Receta Progress: $bytes/$totalBytes")
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>?) {
                    var url = resultData?.get("url") as String?
                    if (url != null && url.startsWith("http://")) {
                        url = url.replaceFirst("http://", "https://")
                        Log.d("CloudinaryUploadSuccess", "URL receta convertida a HTTPS: $url")
                    }
                    Toast.makeText(requireContext(), "Receta subida.", Toast.LENGTH_SHORT).show()
                    onComplete(url)
                }

                override fun onError(requestId: String, error: ErrorInfo?) {
                    Toast.makeText(
                        requireContext(),
                        "Error al subir receta: ${error?.description}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("CloudinaryUpload", "Error receta: ${error?.description}")
                    onComplete(null)
                }

                override fun onReschedule(requestId: String, error: ErrorInfo?) {
                    Log.d("CloudinaryUpload", "Receta Reschedule: ${error?.description}")
                }
            }).dispatch()
    }

    private fun actualizarCompletada(citaId: String, urlReceta: String?, notas: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("usuarios/citas").child(citaId)
        val actualizacion = mutableMapOf<String, Any>(
            "estado" to "Completada",
            "notas" to notas
        )
        urlReceta?.let {
            actualizacion["urlReceta"] = it
        }

        databaseRef.updateChildren(actualizacion)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Cita completada y receta/notas actualizadas!", Toast.LENGTH_SHORT).show()
                cargarDatosDeCita(citaId)
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error al completar cita: ${it.message}")
                Toast.makeText(requireContext(), "Error al completar la cita.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarReprogramar(citaId: String, nuevaFecha: String, nuevaHora: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("usuarios/citas").child(citaId)
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