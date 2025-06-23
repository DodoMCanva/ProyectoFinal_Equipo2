package equipo.dos.citasmedicas

import Persistencia.fakebd
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

class frmLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_login)

        val correo: EditText = findViewById(R.id.et_correo)
        val contrasena: EditText = findViewById(R.id.et_contrasena)

        val btnIniciar: Button = findViewById(R.id.btnIniciarSesion)
        val btnRegistrarse: TextView = findViewById(R.id.tvRegistrarse)
        val btnCntr: TextView = findViewById(R.id.tvOlvidasteContra)


        var intent: Intent

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


            val pacienteAutenticado =autenticarPaciente(email, contra)

            if (pacienteAutenticado != null) {
                val intent = Intent(this, frmPrincipalActivity::class.java)
                intent.putExtra("sesion", pacienteAutenticado)
                startActivity(intent)
                return@setOnClickListener
            }
            val medicoAutenticado = autenticarMedico(email, contra)
            if (medicoAutenticado != null) {
                val intent = Intent(this, frmPrincipalActivity::class.java)
                intent.putExtra("sesion", medicoAutenticado)
                startActivity(intent)
                return@setOnClickListener
            }


        }
        btnRegistrarse.setOnClickListener {
            intent = Intent(this, frmPerfilActivity::class.java)
            startActivity(intent)
        }
        btnCntr.setOnClickListener {
            intent = Intent(this, frmRestablecerContrasenaActivity::class.java)
            startActivity(intent)
        }
    }


    fun autenticarPaciente(correo: String, contrasena: String): paciente? {
        for (p in fakebd.pacientes) {
            if (p.correo == correo && p.contrasena == contrasena) {
                sesion.asignarSesion(p)
                return p
            }
        }
        return null
    }

    fun autenticarMedico(correo: String, contrasena: String): medico? {
        for (m in fakebd.medicos) {
            if (m.correo == correo && m.contrasena == contrasena) {
                sesion.asignarSesion(m)
                return m
            }
        }
        return null
    }
}