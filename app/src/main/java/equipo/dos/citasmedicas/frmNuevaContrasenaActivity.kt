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

            cambiar()

        }
    }

    fun cambiar(){

    }
}

