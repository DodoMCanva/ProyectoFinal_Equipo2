package equipo.dos.citasmedicas

import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import com.bumptech.glide.Glide
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

val UPLOAD_PRESET_CITAS_MEDICAS = "Citas Medicas"

class frmEditarActivity : AppCompatActivity() {
    private lateinit var imgFotoPerfil: ImageView
    private var imagenSeleccionadaUri: Uri? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    imagenSeleccionadaUri = uri
                    imgFotoPerfil.setImageURI(uri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frm_editar)

        imgFotoPerfil = findViewById(R.id.imgFotoPerfil)
        val etNombre = findViewById<EditText>(R.id.etEditarNombre)
        val tvFecha = findViewById<TextView>(R.id.tvFechaRegistroRegistro)
        val etTelefono = findViewById<EditText>(R.id.etEditarTelefono)
        val cbHombre = findViewById<CheckBox>(R.id.cbEditarHombre)
        val cbMujer = findViewById<CheckBox>(R.id.cbEditarMujer)

        // Campos exclusivos del médico
        val etCedula = findViewById<EditText>(R.id.etEditarCedula)
        val etEstado = findViewById<EditText>(R.id.etEditarEstado)
        val etCiudad = findViewById<EditText>(R.id.etEditarCiudad)
        val etCalle = findViewById<EditText>(R.id.etEditarCalle)
        val etNumero = findViewById<EditText>(R.id.etEditarNumero)
        val etCP = findViewById<EditText>(R.id.etEditarCodigoPostal)

        //calendario
        val btnCalendario = findViewById<ImageButton>(R.id.btnCalendarioRegistroMedico)

        btnCalendario.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker =
                DatePickerDialog(this, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    val fechaSeleccionada =
                        String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    tvFecha.text = fechaSeleccionada
                }, anio, mes, dia)

            datePicker.datePicker.maxDate = System.currentTimeMillis()
            datePicker.show()
        }

        cbHombre.setOnCheckedChangeListener(null)
        cbMujer.setOnCheckedChangeListener(null)

        val btnCambiarFoto = findViewById<Button>(R.id.btnCambiarFoto)

        btnCambiarFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImage.launch(intent)
        }
        cargarDatosPerfil()


        cbHombre.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) cbMujer.isChecked = false
        }
        cbMujer.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) cbHombre.isChecked = false
        }

        // Botón cancelar (volver)
        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            finish()
        }

        // Botón guardar
        findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val generoSeleccionado = when {
                cbHombre.isChecked -> "Masculino"
                cbMujer.isChecked -> "Femenino"
                else -> ""
            }

            if (nombre.isEmpty() || telefono.isEmpty() || generoSeleccionado.isEmpty()) {
                Toast.makeText(
                    this,
                    "Nombre, teléfono y género son obligatorios.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val telefonoRegex = Regex("^\\d{10}$")
            if (!telefonoRegex.matches(telefono)) {
                Toast.makeText(
                    this,
                    "El teléfono debe contener exactamente 10 dígitos.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Validaciones SOLO para MÉDICOS
            val sesionActual =
                sesion.sesion
            if (sesionActual is medico) { // Usamos sesionActual para las validaciones
                if (tvFecha.text.toString().trim().isEmpty()) {
                    Toast.makeText(
                        this,
                        "La fecha de nacimiento es obligatoria.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val cedula = etCedula.text.toString().trim()
                if (cedula.isEmpty()) {
                    Toast.makeText(
                        this,
                        "La cédula profesional es obligatoria.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val cedulaRegex = Regex("^[a-zA-Z0-9]{7,8}$")
                if (!cedulaRegex.matches(cedula)) {
                    Toast.makeText(
                        this,
                        "La cédula profesional debe tener entre 7 y 8 caracteres alfanuméricos.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                // Validar dirección solo si es médico
                val estado = etEstado.text.toString().trim()
                val ciudad = etCiudad.text.toString().trim()
                val calle = etCalle.text.toString().trim()
                val numero = etNumero.text.toString().trim()
                val cp = etCP.text.toString().trim()

                if (estado.isEmpty() || ciudad.isEmpty() || calle.isEmpty() || numero.isEmpty() || cp.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Todos los campos de dirección son obligatorios.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val cpRegex = Regex("^\\d{5}$")
                if (!cpRegex.matches(cp)) {
                    Toast.makeText(
                        this,
                        "El código postal debe tener exactamente 5 dígitos.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }


                val fechaSplit = tvFecha.text.toString().trim().split("/")
                if (fechaSplit.size == 3) {
                    val dia = fechaSplit[0].toInt()
                    val mes = fechaSplit[1].toInt() - 1
                    val anio = fechaSplit[2].toInt()

                    val calendarNacimiento = Calendar.getInstance()
                    calendarNacimiento.set(anio, mes, dia)

                    val calendarHoy = Calendar.getInstance()

                    // Validar que no sea futura
                    if (calendarNacimiento.after(calendarHoy)) {
                        Toast.makeText(
                            this,
                            "La fecha de nacimiento no puede ser en el futuro.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    // Validar edad mínima (23 años)
                    val edad =
                        calendarHoy.get(Calendar.YEAR) - calendarNacimiento.get(Calendar.YEAR)
                    val cumpleEsteAnio =
                        calendarHoy.get(Calendar.MONTH) > calendarNacimiento.get(Calendar.MONTH) ||
                                (calendarHoy.get(Calendar.MONTH) == calendarNacimiento.get(Calendar.MONTH) &&
                                        calendarHoy.get(Calendar.DAY_OF_MONTH) >= calendarNacimiento.get(
                                    Calendar.DAY_OF_MONTH
                                ))

                    val edadFinal = if (cumpleEsteAnio) edad else edad - 1

                    if (edadFinal < 23) {
                        Toast.makeText(
                            this,
                            "Debes tener al menos 23 años para registrarte como médico.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                }
            }

            //mostrar el diálogo de confirmación
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_confirmacion_edicion)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val btnAceptar = dialog.findViewById<Button>(R.id.btnConfirmarEdicion)

            btnAceptar.setOnClickListener {
                if (imagenSeleccionadaUri != null) {
                    // Si se seleccionó una nueva imagen, subirla a Cloudinary
                    subirImagenACloudinary(imagenSeleccionadaUri!!) { imageUrl ->
                        // Cuando la imagen se sube correctamente, actualiza el perfil con la nueva URL
                        actualizarPerfil(imageUrl, dialog)
                    }
                } else {
                    // Si no se seleccionó una nueva imagen, obtiene la URL actual y solo actualiza el resto de datos
                    val currentImageUrl = when (val s = Persistencia.sesion.sesion) {
                        is medico -> s.fotoPerfil
                        is paciente -> s.fotoPerfil
                        else -> null
                    }
                    actualizarPerfil(currentImageUrl, dialog)
                }
            }
            dialog.show()
        }
    }

    // Función para cargar los datos del perfil y la foto (usando Glide)
    private fun cargarDatosPerfil() {
        val sesionActual = Persistencia.sesion.sesion

        val etNombre = findViewById<EditText>(R.id.etEditarNombre)
        val tvCorreo = findViewById<TextView>(R.id.tvEditarCorreo)
        val tvFecha = findViewById<TextView>(R.id.tvFechaRegistroRegistro)
        val etTelefono = findViewById<EditText>(R.id.etEditarTelefono)
        val cbHombre = findViewById<CheckBox>(R.id.cbEditarHombre)
        val cbMujer = findViewById<CheckBox>(R.id.cbEditarMujer)
        val seccionMedico = findViewById<LinearLayout>(R.id.editarPerfilMedicoSeccion)
        val spEspecialidad = findViewById<Spinner>(R.id.spEditarEspecialidad)
        val etCedula = findViewById<EditText>(R.id.etEditarCedula)
        val etEstado = findViewById<EditText>(R.id.etEditarEstado)
        val etCiudad = findViewById<EditText>(R.id.etEditarCiudad)
        val etCalle = findViewById<EditText>(R.id.etEditarCalle)
        val etNumero = findViewById<EditText>(R.id.etEditarNumero)
        val etCP = findViewById<EditText>(R.id.etEditarCodigoPostal)


        when (sesionActual) {
            is medico -> {
                val m = sesionActual
                etNombre.setText(m.nombre)
                tvCorreo.text = m.correo
                tvFecha.text = m.fechaNacimiento
                etTelefono.setText(m.telefono)
                when (m.genero?.trim()?.lowercase()) {
                    "masculino" -> cbHombre.isChecked = true
                    "femenino" -> cbMujer.isChecked = true
                }
                seccionMedico.visibility = LinearLayout.VISIBLE
                val especialidades = listOf(
                    "Cardiología", "Pediatría", "Dermatología", "Ginecología",
                    "Neurología", "Psiquiatría", "Oftalmología", "Medicina General"
                )
                val adapterEspecialidades =
                    ArrayAdapter(this, R.layout.item_spinner_especialidad, especialidades)
                adapterEspecialidades.setDropDownViewResource(R.layout.item_spinner_especialidad)
                spEspecialidad.adapter = adapterEspecialidades
                val posicion = adapterEspecialidades.getPosition(m.especialidad)
                spEspecialidad.setSelection(posicion)
                etCedula.setText(m.cedula)
                etEstado.setText(m.direccion?.estado)
                etCiudad.setText(m.direccion?.ciudad)
                etCalle.setText(m.direccion?.calle)
                etNumero.setText(m.direccion?.numero)
                etCP.setText(m.direccion?.cp)

                if (imagenSeleccionadaUri == null) {
                    if (m.fotoPerfil != null && m.fotoPerfil!!.startsWith("http")) {
                        Glide.with(this)
                            .load(m.fotoPerfil) // Aquí va la URL de Cloudinary
                            .placeholder(R.drawable.usuario) // Imagen por defecto mientras carga
                            .error(R.drawable.usuario)     // Imagen por defecto si hay error
                            .into(imgFotoPerfil)
                        Log.d("CargarPerfil", "Cargando foto de médico desde URL: ${m.fotoPerfil}")
                    } else {
                        Glide.with(this)
                            .load(R.drawable.usuario)
                            .into(imgFotoPerfil)
                        Log.d("CargarPerfil", "Cargando foto de médico desde drawable por defecto.")
                    }
                } else {
                    Log.d(
                        "CargarPerfil",
                        "Manteniendo previsualización de imagen seleccionada para médico."
                    )
                }
            }

            is paciente -> {
                val p = sesionActual
                etNombre.setText(p.nombre)
                tvCorreo.text = p.correo
                tvFecha.text = p.fechaNacimiento
                etTelefono.setText(p.telefono)
                when (p.genero?.trim()?.lowercase()) {
                    "masculino" -> cbHombre.isChecked = true
                    "femenino" -> cbMujer.isChecked = true
                }
                seccionMedico.visibility = LinearLayout.GONE

                if (imagenSeleccionadaUri == null) {
                    if (p.fotoPerfil != null && p.fotoPerfil!!.startsWith("http")) {
                        Glide.with(this)
                            .load(p.fotoPerfil)
                            .placeholder(R.drawable.usuario) // Imagen por defecto mientras carga
                            .error(R.drawable.usuario)     // Imagen por defecto si hay error
                            .into(imgFotoPerfil)
                        Log.d(
                            "CargarPerfil",
                            "Cargando foto de paciente desde URL: ${p.fotoPerfil}"
                        )
                    } else {
                        Glide.with(this)
                            .load(R.drawable.usuario)
                            .into(imgFotoPerfil)
                        Log.d(
                            "CargarPerfil",
                            "Cargando foto de paciente desde drawable por defecto."
                        )
                    }
                } else {
                    Log.d(
                        "CargarPerfil",
                        "Manteniendo previsualización de imagen seleccionada para paciente."
                    )
                }
            }

            else -> {
                Toast.makeText(this, "No se pudo cargar la sesión", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    private fun subirImagenACloudinary(uri: Uri, onComplete: (String?) -> Unit) {
        Toast.makeText(this, "Subiendo imagen...", Toast.LENGTH_SHORT).show()
        MediaManager.get().upload(uri)
            .unsigned(UPLOAD_PRESET_CITAS_MEDICAS)
            .option("folder", "perfiles")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    Log.d("CloudinaryUpload", "Upload start for $requestId")
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    Log.d("CloudinaryUpload", "Progress: $bytes/$totalBytes")
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>?) {
                    var url = resultData?.get("url") as String?
                    if (url != null && url.startsWith("http://")) {
                        url = url.replaceFirst("http://", "https://")
                        Log.d("CloudinaryUploadSuccess", "URL convertida a HTTPS: $url")
                    }
                    Toast.makeText(this@frmEditarActivity, "Imagen subida", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("CloudinaryUpload", "Success: $url")
                    onComplete(url)
                }

                override fun onError(requestId: String, error: ErrorInfo?) {
                    Toast.makeText(
                        this@frmEditarActivity,
                        "Error al subir imagen: ${error?.description}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("CloudinaryUpload", "Error: ${error?.description}")
                    onComplete(null)
                }

                override fun onReschedule(requestId: String, error: ErrorInfo?) {
                    Log.d("CloudinaryUpload", "Reschedule: ${error?.description}")
                }
            }).dispatch()
    }

    private fun actualizarPerfil(imageUrl: String?, dialog: Dialog) {
        val nombre = findViewById<EditText>(R.id.etEditarNombre).text.toString().trim()
        val telefono = findViewById<EditText>(R.id.etEditarTelefono).text.toString().trim()
        val cbHombre = findViewById<CheckBox>(R.id.cbEditarHombre)
        val cbMujer = findViewById<CheckBox>(R.id.cbEditarMujer)
        val tvFecha = findViewById<TextView>(R.id.tvFechaRegistroRegistro)
        val spEspecialidad = findViewById<Spinner>(R.id.spEditarEspecialidad)
        val etCedula = findViewById<EditText>(R.id.etEditarCedula)
        val etEstado = findViewById<EditText>(R.id.etEditarEstado)
        val etCiudad = findViewById<EditText>(R.id.etEditarCiudad)
        val etCalle = findViewById<EditText>(R.id.etEditarCalle)
        val etNumero = findViewById<EditText>(R.id.etEditarNumero)
        val etCP = findViewById<EditText>(R.id.etEditarCodigoPostal)

        val generoSeleccionado = when {
            cbHombre.isChecked -> "Masculino"
            cbMujer.isChecked -> "Femenino"
            else -> ""
        }
        val sesionActual = sesion.obtenerSesion()

        if (sesionActual == null) {
            Toast.makeText(this, "Error: Sesión no válida para actualizar.", Toast.LENGTH_SHORT)
                .show()
            dialog.dismiss()
            return
        }

        when (sesionActual) {
            is medico -> {
                sesionActual.nombre = nombre
                sesionActual.telefono = telefono
                sesionActual.genero = generoSeleccionado
                sesionActual.especialidad = spEspecialidad.selectedItem.toString()
                sesionActual.cedula = etCedula.text.toString().trim()
                sesionActual.direccion?.estado = etEstado.text.toString().trim()
                sesionActual.direccion?.ciudad = etCiudad.text.toString().trim()
                sesionActual.direccion?.calle = etCalle.text.toString().trim()
                sesionActual.direccion?.numero = etNumero.text.toString().trim()
                sesionActual.direccion?.cp = etCP.text.toString().trim()
                sesionActual.fechaNacimiento = tvFecha.text.toString().trim()
                imageUrl?.let { sesionActual.fotoPerfil = it }
                sesionActual.uid?.let { uid ->
                    FirebaseDatabase.getInstance().getReference("usuarios/medicos").child(uid)
                        .setValue(sesionActual)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Perfil de médico/paciente actualizado en Firebase.",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d(
                                "EditarPerfil",
                                "Perfil actualizado en Firebase. Foto URL en Firebase: ${sesionActual.fotoPerfil}"
                            )
                            sesion.asignarSesion(sesionActual)
                            Log.d(
                                "EditarPerfil",
                                "Sesión global Persistencia.sesion.sesion actualizada. Foto URL: ${(sesion.obtenerSesion() as? medico)?.fotoPerfil ?: (sesion.obtenerSesion() as? paciente)?.fotoPerfil}"
                            )
                            val intent = Intent(this, frmPrincipalActivity::class.java)
                            startActivity(intent)
                            dialog.dismiss()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Error al actualizar médico en Firebase: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.e("FirebaseUpdate", "Error updating medico: ${e.message}")
                            dialog.dismiss()
                        }
                } ?: run {
                    Toast.makeText(
                        this,
                        "Error: UID del médico no encontrado para actualizar.",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                }
            }

            is paciente -> {
                sesionActual.nombre = nombre
                sesionActual.telefono = telefono
                sesionActual.genero = generoSeleccionado
                sesionActual.fechaNacimiento = tvFecha.text.toString().trim()
                imageUrl?.let { sesionActual.fotoPerfil = it }
                Log.d(
                    "EditarPerfil",
                    "Foto de perfil asignada en memoria: ${sesionActual.fotoPerfil}"
                )
                sesionActual.uid?.let { uid ->
                    FirebaseDatabase.getInstance().getReference("usuarios/pacientes").child(uid)
                        .setValue(sesionActual) // Guarda el objeto paciente actualizado en Firebase
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Perfil de paciente actualizado en Firebase.",
                                Toast.LENGTH_SHORT
                            ).show()
                            sesion.asignarSesion(sesionActual)
                            val intent = Intent(this, frmPrincipalActivity::class.java)
                            startActivity(intent)
                            dialog.dismiss()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Error al actualizar paciente en Firebase: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.e("FirebaseUpdate", "Error updating paciente: ${e.message}")
                            dialog.dismiss()
                        }
                } ?: run {
                    Toast.makeText(
                        this,
                        "Error: UID del paciente no encontrado para actualizar.",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                }
            }
            
            else -> {
                Toast.makeText(this, "Tipo de sesión desconocido.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
    }
}