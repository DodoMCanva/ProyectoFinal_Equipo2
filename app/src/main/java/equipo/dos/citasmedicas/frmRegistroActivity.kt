package equipo.dos.citasmedicas

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class frmRegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_registro)

        val etNombre: EditText = findViewById(R.id.etNombreRegistro)
        val etCorreo: EditText = findViewById(R.id.etRegistroCorreo)
        val tvFecha: TextView = findViewById(R.id.tvFechaRegistroRegistro)
        val etContra: EditText = findViewById(R.id.etContraRegistro)
        val etConfContra: EditText = findViewById(R.id.etnuevaContraRegistro)
        val etTelefono: EditText = findViewById(R.id.etTelefonoRegistro)
        val cbHombre: CheckBox = findViewById(R.id.cbHombreRegistro)
        val cbMujer: CheckBox = findViewById(R.id.cbMujerRegistro)
        val btnRegistrarse: Button = findViewById(R.id.btnRegistrarse)
        val btnCalendario: ImageButton = findViewById(R.id.btnCalendarioRegistroPaciente)

        btnCalendario.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val selectorFecha = DatePickerDialog(this, { _, y, m, d ->
                val fechaFormateada = String.format("%02d/%02d/%04d", d, m + 1, y)
                tvFecha.text = fechaFormateada
            }, anio, mes, dia)

            selectorFecha.datePicker.maxDate = System.currentTimeMillis()
            selectorFecha.show()
        }

        //checkBox de genero
        cbHombre.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) cbMujer.isChecked = false
        }

        cbMujer.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) cbHombre.isChecked = false
        }

        btnRegistrarse.setOnClickListener {
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

            if (nombre.isEmpty() || correo.isEmpty() || fecha.isEmpty() ||
                contra.isEmpty() || confContra.isEmpty() || telefono.isEmpty() || genero.isEmpty()
            ) {
                Toast.makeText(this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
            if (!emailRegex.matches(correo)) {
                Toast.makeText(this, "Correo electrónico inválido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&#+=_.-])[A-Za-z\\d@\$!%*?&#+=_.-]{8,}$")
            if (!passwordRegex.matches(contra)) {
                Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 8 caracteres, incluyendo una mayúscula, números y un carácter especial.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            if (contra != confContra) {
                Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val telefonoRegex = Regex("^\\d{10}$")
            if (!telefonoRegex.matches(telefono)) {
                Toast.makeText(this, "El número de teléfono debe tener 10 digitos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val auth = FirebaseAuth.getInstance()
            val database = FirebaseDatabase.getInstance().reference

            auth.fetchSignInMethodsForEmail(correo).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (!signInMethods.isNullOrEmpty()) {
                        Toast.makeText(this, "Este correo ya está registrado.", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }

                    // Si no está registrado, crear cuenta
                    auth.createUserWithEmailAndPassword(correo, contra)
                        .addOnCompleteListener { regTask ->
                            if (regTask.isSuccessful) {
                                val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                                val paciente = mapOf(
                                    "nombre" to nombre,
                                    "correo" to correo,
                                    "fechaNacimiento" to fecha,
                                    "telefono" to telefono,
                                    "genero" to genero,
                                    "tipo" to "paciente"
                                )

                                database.child("usuarios").child("pacientes").child(uid).setValue(paciente)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Paciente registrado correctamente", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, frmLoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Error al guardar datos del paciente.", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(this, "Error al registrar: ${regTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Error al verificar el correo.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}