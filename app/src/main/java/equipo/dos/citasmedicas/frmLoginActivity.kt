package equipo.dos.citasmedicas


import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
                            val db = FirebaseDatabase.getInstance().getReference("usuarios")

                            db.child("medicos").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                               override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        val medicoData = snapshot.getValue(medico::class.java)
                                        if (medicoData != null) {
                                            medicoData.uid = uid
                                            sesion.asignarSesion(medicoData)
                                            Log.d("Login", "Sesión de médico iniciada: UID = $uid")
                                            val intent = Intent(this@frmLoginActivity, frmPrincipalActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            Toast.makeText(this@frmLoginActivity, "Error al obtener datos del médico.", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        db.child("pacientes").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot.exists()) {
                                                    val pacienteData = snapshot.getValue(paciente::class.java)
                                                    if (pacienteData != null) {
                                                        pacienteData.uid = uid
                                                        sesion.asignarSesion(pacienteData)
                                                        Log.d("Login", "Sesión de paciente iniciada: UID = $uid")
                                                        val intent = Intent(this@frmLoginActivity, frmPrincipalActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    } else {
                                                        Toast.makeText(this@frmLoginActivity, "Error al obtener datos del paciente.", Toast.LENGTH_SHORT).show()
                                                    }
                                                } else {
                                                    Toast.makeText(this@frmLoginActivity, "Usuario no encontrado en la base de datos.", Toast.LENGTH_SHORT).show()
                                                    auth.signOut() // Cierra la sesión de Auth para evitar problemas
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                Log.e("Firebase", "Error al leer datos del usuario: ${error.message}")
                                                Toast.makeText(this@frmLoginActivity, "Error al leer datos del usuario.", Toast.LENGTH_SHORT).show()
                                            }
                                        })
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("Firebase", "Error al leer datos del médico: ${error.message}")
                                    Toast.makeText(this@frmLoginActivity, "Error al leer datos del médico.", Toast.LENGTH_SHORT).show()
                                }
                            })

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