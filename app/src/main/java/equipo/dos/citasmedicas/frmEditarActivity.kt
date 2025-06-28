package equipo.dos.citasmedicas

import Persistencia.medico
import Persistencia.paciente
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import equipo.dos.citasmedicas.databinding.ActivityFrmEditarBinding
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding
import java.util.Calendar

class frmEditarActivity : AppCompatActivity() {

    private lateinit var imgFotoPerfil: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frm_editar)



// Obtener referencia de todos los campos
        imgFotoPerfil = findViewById(R.id.imgFotoPerfil)
        val etNombre = findViewById<EditText>(R.id.etEditarNombre)
        val tvCorreo = findViewById<TextView>(R.id.tvEditarCorreo)
        val tvFecha = findViewById<TextView>(R.id.tvFechaRegistroRegistro)
        val etTelefono = findViewById<EditText>(R.id.etEditarTelefono)
        val cbHombre = findViewById<CheckBox>(R.id.cbEditarHombre)
        val cbMujer = findViewById<CheckBox>(R.id.cbEditarMujer)

        // Campos exclusivos del médico
        val seccionMedico = findViewById<LinearLayout>(R.id.editarPerfilMedicoSeccion)
        val spEspecialidad = findViewById<Spinner>(R.id.spEditarEspecialidad)
        val etCedula = findViewById<EditText>(R.id.etEditarCedula)
        val etEstado = findViewById<EditText>(R.id.etEditarEstado)
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

            val datePicker = DatePickerDialog(this, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val fechaSeleccionada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                tvFecha.text = fechaSeleccionada
            }, anio, mes, dia)

            datePicker.datePicker.maxDate = System.currentTimeMillis()
            datePicker.show()
        }




        cbHombre.setOnCheckedChangeListener(null)
        cbMujer.setOnCheckedChangeListener(null)

        // Recuperar objeto recibido
        val sesion = Persistencia.sesion.sesion

        // Identificar tipo
        when (sesion) {
            is medico -> {
                val m = sesion

                val fotoResId = resources.getIdentifier(m.fotoPerfil, "drawable", packageName)
                if (fotoResId != 0) {
                    imgFotoPerfil.setImageResource(fotoResId)
                }

                etNombre.setText(m.nombre)
                tvCorreo.text = m.correo
                tvFecha.text = m.fechaNacimiento
                etTelefono.setText(m.telefono)

                // Cargar género correctamente
                when (m.genero.trim().lowercase()) {
                    "masculino" -> cbHombre.isChecked = true
                    "femenino" -> cbMujer.isChecked = true
                }

                seccionMedico.visibility = LinearLayout.VISIBLE

                val especialidades = listOf(
                    "Cardiología", "Pediatría", "Dermatología", "Ginecología",
                    "Neurología", "Psiquiatría", "Oftalmología", "Medicina General"
                )

                val adapterEspecialidades = ArrayAdapter(
                    this,
                    R.layout.item_spinner_especialidad,
                    especialidades
                )
                adapterEspecialidades.setDropDownViewResource(R.layout.item_spinner_especialidad)
                spEspecialidad.adapter = adapterEspecialidades

                val posicion = adapterEspecialidades.getPosition(m.especialidad)
                spEspecialidad.setSelection(posicion)

                etCedula.setText(m.cedula)
                etEstado.setText(m.estado)
                etCalle.setText(m.calle)
                etNumero.setText(m.numero)
                etCP.setText(m.cp)
            }

            is paciente -> {
                val p = sesion

                val fotoResId = resources.getIdentifier(p.fotoPerfil, "drawable", packageName)
                if (fotoResId != 0) {
                    imgFotoPerfil.setImageResource(fotoResId)
                }

                etNombre.setText(p.nombre)
                tvCorreo.text = p.correo
                tvFecha.text = p.fechaNacimiento
                etTelefono.setText(p.telefono)

                // Cargar género correctamente
                when (p.genero.trim().lowercase()) {
                    "masculino" -> cbHombre.isChecked = true
                    "femenino" -> cbMujer.isChecked = true
                }

                seccionMedico.visibility = LinearLayout.GONE
            }
            else -> {
                Toast.makeText(this, "No se pudo cargar la sesión", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

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
                Toast.makeText(this, "Nombre, teléfono y género son obligatorios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val telefonoRegex = Regex("^\\d{10}$")
            if (!telefonoRegex.matches(telefono)) {
                Toast.makeText(this, "El teléfono debe contener exactamente 10 dígitos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cp = etCP.text.toString().trim()
            val cpRegex = Regex("^\\d{5}$")
            if (!cpRegex.matches(cp)) {
                Toast.makeText(this, "El código postal debe tener exactamente 5 dígitos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (sesion is medico) {
                if (tvFecha.text.toString().trim().isEmpty()) {
                    Toast.makeText(this, "La fecha de nacimiento es obligatoria.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (etCedula.text.toString().trim().isEmpty()) {
                    Toast.makeText(this, "La cédula profesional es obligatoria.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val cedula = etCedula.text.toString().trim()
                val cedulaRegex = Regex("^\\d{7,8}$")
                if (!cedulaRegex.matches(cedula)) {
                    Toast.makeText(this, "La cédula profesional debe tener entre 7 y 8 dígitos numéricos.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (etEstado.text.toString().trim().isEmpty() ||
                    etCalle.text.toString().trim().isEmpty() ||
                    etNumero.text.toString().trim().isEmpty() ||
                    etCP.text.toString().trim().isEmpty()) {
                    Toast.makeText(this, "Todos los campos de dirección son obligatorios.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Validar fecha que no sea futura
                val fechaSplit = tvFecha.text.toString().trim().split("/")
                if (fechaSplit.size == 3) {
                    val dia = fechaSplit[0].toInt()
                    val mes = fechaSplit[1].toInt() - 1
                    val anio = fechaSplit[2].toInt()

                    val calendarNacimiento = Calendar.getInstance()
                    calendarNacimiento.set(anio, mes, dia)
                    if (calendarNacimiento.after(Calendar.getInstance())) {
                        Toast.makeText(this, "La fecha de nacimiento no puede ser en el futuro.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
            }

            //mostrar el diálogo
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_confirmacion_edicion)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val btnAceptar = dialog.findViewById<Button>(R.id.btnConfirmarEdicion)

            btnAceptar.setOnClickListener {
                if (sesion is medico) {
                    val m = sesion
                    m.nombre = nombre
                    m.telefono = telefono
                    m.genero = generoSeleccionado
                    m.especialidad = spEspecialidad.selectedItem.toString()
                    m.cedula = etCedula.text.toString().trim()
                    m.estado = etEstado.text.toString().trim()
                    m.calle = etCalle.text.toString().trim()
                    m.numero = etNumero.text.toString().trim()
                    m.cp = etCP.text.toString().trim()
                    m.fechaNacimiento = tvFecha.text.toString().trim()
                } else if (sesion is paciente) {
                    val p = sesion
                    p.nombre = nombre
                    p.telefono = telefono
                    p.genero = generoSeleccionado
                }

                val intent = Intent(this, frmMiPerfilActivity::class.java)
                intent.putExtra("sesion", sesion)
                startActivity(intent)
                dialog.dismiss()
            }

            dialog.show()
            Toast.makeText(this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show()
        }

    }
}