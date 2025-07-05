package equipo.dos.citasmedicas

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import Persistencia.direccion
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class frmRegistroMedicoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_registro_medico)

        val btnRegistrarseMedico: Button = findViewById(R.id.btnRegistrarseMedico)
        val etNombre = findViewById<EditText>(R.id.etNombreRegistroMedico)
        val etCorreo = findViewById<EditText>(R.id.etRegistroCorreoMedico)
        val tvFecha = findViewById<TextView>(R.id.tvFechaRegistroRegistro)
        val etContra = findViewById<EditText>(R.id.etContraRegistroMedico)
        val etConfContra = findViewById<EditText>(R.id.etnuevaContraRegistroMedico)
        val etTelefono = findViewById<EditText>(R.id.etTelefonoRegistroMedico)
        val cbHombre = findViewById<CheckBox>(R.id.cbHombreRegistroMedico)
        val cbMujer = findViewById<CheckBox>(R.id.cbMujerRegistroMedico)
        val spEspecialidad = findViewById<Spinner>(R.id.spEspecialidad)
        val etCedula = findViewById<EditText>(R.id.etCedulaMedico)
        val etEstado = findViewById<EditText>(R.id.etEstadoMedico)
        val etCiudad = findViewById<EditText>(R.id.etCiudadMedico)
        val etCalle = findViewById<EditText>(R.id.etCalleMedico)
        val etNumero = findViewById<EditText>(R.id.etNumeroMedico)
        val etCodigoPostal = findViewById<EditText>(R.id.etCodigoPostalMedico)
        val btnCalendario: ImageButton = findViewById(R.id.btnCalendarioRegistroMedico)


        btnCalendario.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val selectorFecha = DatePickerDialog(this, { _, y, m, d ->
                val fechaSeleccionada = Calendar.getInstance()
                fechaSeleccionada.set(y, m, d)

                val hoy = Calendar.getInstance()
                val edad = hoy.get(Calendar.YEAR) - fechaSeleccionada.get(Calendar.YEAR)

                // Ajuste si aún no cumple años este año
                if (hoy.get(Calendar.DAY_OF_YEAR) < fechaSeleccionada.get(Calendar.DAY_OF_YEAR)) {
                    fechaSeleccionada.add(Calendar.YEAR, 1)
                }

                // Validar edad mínima de 23 años
                val cumple23 = Calendar.getInstance()
                cumple23.add(Calendar.YEAR, -23)

                if (fechaSeleccionada.after(cumple23)) {
                    Toast.makeText(this, "El médico debe tener al menos 23 años.", Toast.LENGTH_SHORT).show()
                } else {
                    val fechaFormateada = String.format("%02d/%02d/%04d", d, m + 1, y)
                    tvFecha.text = fechaFormateada
                }

            }, anio, mes, dia)

            selectorFecha.datePicker.maxDate = System.currentTimeMillis()
            selectorFecha.show()
        }

        //checkbos de genero
        cbHombre.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) cbMujer.isChecked = false
        }
        cbMujer.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) cbHombre.isChecked = false
        }

        // Llenar el spinner con las especialidades médicas
        val especialidades = listOf(
            "Cardiología", "Pediatría", "Dermatología", "Ginecología",
            "Neurología", "Psiquiatría", "Oftalmología", "Medicina General"
        )

        val adapterEspecialidades = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            especialidades
        )
        adapterEspecialidades.setDropDownViewResource(R.layout.item_spinner_especialidad)
        spEspecialidad.adapter = adapterEspecialidades


        btnRegistrarseMedico.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val fecha = tvFecha.text.toString().trim()
            val contra = etContra.text.toString()
            val confContra = etConfContra.text.toString()
            val telefono = etTelefono.text.toString().trim()
            val genero = when {
                cbHombre.isChecked -> "Masculino"
                cbMujer.isChecked -> "Femenino"
                else -> ""
            }
            val especialidad = spEspecialidad.selectedItem?.toString()?.trim() ?: ""
            val cedula = etCedula.text.toString().trim()
            val estado = etEstado.text.toString().trim()
            val ciudad = etCiudad.text.toString().trim()
            val calle = etCalle.text.toString().trim()
            val numero = etNumero.text.toString().trim()
            val codigoPostal = etCodigoPostal.text.toString().trim()

            // Validación de campos vacíos
            if (nombre.isEmpty() || correo.isEmpty() || fecha.isEmpty() || contra.isEmpty() ||
                confContra.isEmpty() || telefono.isEmpty() || genero.isEmpty() || especialidad.isEmpty() ||
                cedula.isEmpty() || estado.isEmpty() || ciudad.isEmpty() || calle.isEmpty() ||
                numero.isEmpty() || codigoPostal.isEmpty()
            ) {
                Toast.makeText(this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación de formato de correo
            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
            if (!emailRegex.matches(correo)) {
                Toast.makeText(this, "Correo electrónico inválido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación de contraseña segura
            val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$")
            if (!passwordRegex.matches(contra)) {
                Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 8 caracteres, incluir una mayúscula, una minúscula, un número y un carácter especial.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            if (contra != confContra) {
                Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación de teléfono
            val telefonoRegex = Regex("^\\d{10}$")
            if (!telefonoRegex.matches(telefono)) {
                Toast.makeText(this, "El teléfono debe tener exactamente 10 dígitos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación de cédula alfanumérica (7–8 caracteres, mínimo una letra y un número)
            val cedulaRegex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{7,8}$")
            if (!cedulaRegex.matches(cedula)) {
                Toast.makeText(this, "La cédula debe tener entre 7 y 8 caracteres alfanuméricos (mínimo una letra y un número).", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val codigoPostalRegex = Regex("^\\d{5}$")
            if (!codigoPostalRegex.matches(codigoPostal)) {
                Toast.makeText(this, "El código postal debe tener 5 dígitos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val numeroRegex = Regex("^\\d+$")
            if (!numeroRegex.matches(numero)) {
                Toast.makeText(this, "El número de domicilio debe ser solo numérico.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificar si el correo ya existe en Firebase
            val auth = FirebaseAuth.getInstance()
            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(correo)
                .addOnSuccessListener { result ->
                    val methods = result.signInMethods
                    if (methods != null && methods.isNotEmpty()) {
                        Toast.makeText(this, "El correo ya está registrado.", Toast.LENGTH_SHORT).show()
                    } else {
                        // Crear cuenta en Firebase
                        auth.createUserWithEmailAndPassword(correo, contra)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                                    val direccionObj = direccion(
                                        estado = estado,
                                        ciudad = ciudad,
                                        calle = calle,
                                        numero = numero,
                                        cp = codigoPostal
                                    )

                                    val medico = mapOf(
                                        "uid" to uid,
                                        "nombre" to nombre,
                                        "correo" to correo,
                                        "fechaNacimiento" to fecha,
                                        "telefono" to telefono,
                                        "genero" to genero,
                                        "especialidad" to especialidad,
                                        "cedula" to cedula,
                                        "direccion" to direccionObj,
                                        "fotoPerfil" to "usuario1",
                                        "tipo" to "medico"
                                    )

                                    FirebaseDatabase.getInstance().reference
                                        .child("usuarios").child("medicos").child(uid)
                                        .setValue(medico)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Médico registrado correctamente.", Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this, frmLoginActivity::class.java))
                                            finish()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this, "Error al guardar datos del médico.", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    val exception = task.exception
                                    if (exception is FirebaseAuthUserCollisionException) {
                                        Toast.makeText(this, "El correo ya está registrado.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this, "Error al registrar: ${exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al verificar el correo: ${it.message}", Toast.LENGTH_SHORT).show()
                }

        }
    }
}