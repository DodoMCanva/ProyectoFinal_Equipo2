package equipo.dos.citasmedicas


import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class frmLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_login)

        auth = FirebaseAuth.getInstance()

        val correo: EditText = findViewById(R.id.et_correo)
        val contrasena: EditText = findViewById(R.id.et_contrasena)

        val btnIniciar: Button = findViewById(R.id.btnIniciarSesion)
        val btnRegistrarse: TextView = findViewById(R.id.tvRegistrarse)
        val btnCntr: TextView = findViewById(R.id.tvOlvidasteContra)

        btnIniciar.setOnClickListener {
            val email = correo.text.toString().trim()
            val contra = contrasena.text.toString().trim()

            if (email.isEmpty() || contra.isEmpty()) {
                Toast.makeText(
                    this,
                    "Por favor, ingresa tu correo y contraseña.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
            if (!emailRegex.matches(email)) {
                Toast.makeText(
                    this,
                    "Por favor, ingresa un correo válido.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, contra)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            val uid = user.uid
                            val db = FirebaseDatabase.getInstance().reference

                            // --- PASO 1: Buscar en la base de datos de MÉDICOS ---
                            db.child("usuarios").child("medicos").child(uid).get().addOnSuccessListener { snapshotMedico ->
                                if (snapshotMedico.exists()) {
                                    val medicoData = snapshotMedico.getValue(medico::class.java)
                                    if (medicoData != null) {
                                        // --- AQUI ASIGNAS LA SESION ---
                                        sesion.asignarSesion(medicoData)
                                        val intent = Intent(this, frmPrincipalActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Error al obtener datos del médico.", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    db.child("usuarios").child("pacientes").child(uid).get().addOnSuccessListener { snapshotPaciente ->
                                        if (snapshotPaciente.exists()) {
                                            val pacienteData = snapshotPaciente.getValue(paciente::class.java)
                                            if (pacienteData != null) {
                                                // --- AQUI ASIGNAS LA SESION ---
                                                sesion.asignarSesion(pacienteData)
                                                val intent = Intent(this, frmPrincipalActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            } else {
                                                Toast.makeText(this, "Error al obtener datos del paciente.", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            // No está en ninguna categoría
                                            Toast.makeText(this, "Usuario no encontrado en base de datos.", Toast.LENGTH_SHORT).show()
                                        }
                                    }.addOnFailureListener {
                                        Toast.makeText(this, "Error al leer datos del usuario: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }.addOnFailureListener {
                                Toast.makeText(this, "Error al leer datos del usuario: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Correo o contraseña incorrectos.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        btnRegistrarse.setOnClickListener {
            val intent = Intent(this, frmPerfilActivity::class.java)
            startActivity(intent)
        }
        btnCntr.setOnClickListener {
            val intent = Intent(this, frmRestablecerContrasenaActivity::class.java)
            startActivity(intent)
        }
    }


}