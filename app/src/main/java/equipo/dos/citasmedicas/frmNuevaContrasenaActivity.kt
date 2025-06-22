package equipo.dos.citasmedicas

import Persistencia.fakebd
import Persistencia.medico
import Persistencia.paciente
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class frmNuevaContrasenaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_nueva_contrasena)

        val etNuevaContrasena = findViewById<EditText>(R.id.et_nuevaContrasena)
        val etConfirmarContrasena = findViewById<EditText>(R.id.et_confirmarContrasena)
        val btnCambiar = findViewById<Button>(R.id.btnCambiarContra)

        val correo = intent.getStringExtra("correo") ?: ""

        if (correo.isEmpty()) {
            Toast.makeText(this, "Error: Correo no proporcionado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        btnCambiar.setOnClickListener {
            val nueva = etNuevaContrasena.text.toString().trim()
            val confirmar = etConfirmarContrasena.text.toString().trim()

            if (nueva.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(this, "No dejes campos vacíos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nueva != confirmar) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            var usuarioEncontrado = false

            val paciente = fakebd.pacientes.find { it.correo == correo }
            if (paciente != null) {
                paciente.contrasena = nueva
                usuarioEncontrado = true
            } else {
                val medico = fakebd.medicos.find { it.correo == correo }
                if (medico != null) {
                    medico.contrasena = nueva
                    usuarioEncontrado = true
                }
            }

            if (usuarioEncontrado) {
                Toast.makeText(this, "Contraseña actualizada exitosamente.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, frmLoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error: Usuario no encontrado para el correo proporcionado.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

