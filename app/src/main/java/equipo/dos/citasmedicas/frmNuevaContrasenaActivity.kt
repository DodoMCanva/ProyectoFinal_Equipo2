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

//mientras
    var listaPacientes : ArrayList<paciente> = ArrayList<paciente>()
    var listaMedicos : ArrayList<medico> = ArrayList<medico>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_nueva_contrasena)

        val etNuevaContrasena = findViewById<EditText>(R.id.et_nuevaContrasena)
        val etConfirmarContrasena = findViewById<EditText>(R.id.et_confirmarContrasena)
        val btnCambiar = findViewById<Button>(R.id.btnCambiarContra)

        val correo = intent.getStringExtra("correo") ?: ""

        btnCambiar.setOnClickListener {
            val nueva = etNuevaContrasena.text.toString()
            val confirmar = etConfirmarContrasena.text.toString()

            if (nueva.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(this, "No dejes campos vacíos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nueva != confirmar) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //val paciente = fakebd.pacientes.find { it.correo == correo }
            val paciente = listaPacientes.find { it.correo == correo }
            if (paciente != null) {
                paciente.contrasena = nueva
            } else {
                //val medico = fakebd.medicos.find { it.correo == correo }
                val medico = listaMedicos.find { it.correo == correo }
                if (medico != null) {
                    //medico.contrasena = nueva**********************************************
                } else {
                    Toast.makeText(this, "Correo no encontrado", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, frmLoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

