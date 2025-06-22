package equipo.dos.citasmedicas

import Persistencia.fakebd
import Persistencia.medico
import Persistencia.paciente
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class frmRestablecerContrasenaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_restablecer_contrasena)

        val etCorreo = findViewById<EditText>(R.id.et_correoResContra)
        val btnEnviarCodigo = findViewById<Button>(R.id.btnEnviarCodigo)

        btnEnviarCodigo.setOnClickListener {
            val correoIngresado = etCorreo.text.toString().trim()

            if (correoIngresado.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa tu correo.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (buscarCorreo(correoIngresado)) {
                val intent = Intent(this, frmVerificaIdentidadActivity::class.java)
                intent.putExtra("correo", correoIngresado)
                startActivity(intent)
            } else {
                Toast.makeText(this, "El correo no está registrado.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun buscarCorreo(correo: String): Boolean {

        val pacienteEncontrado = fakebd.pacientes.find { it.correo == correo }
        val medicoEncontrado = fakebd.medicos.find { it.correo == correo }

        return pacienteEncontrado != null || medicoEncontrado != null
    }
}