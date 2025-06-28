package equipo.dos.citasmedicas

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import modulos.EmailSender

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

            buscarCorreo(correoIngresado) { existe ->
                if (existe) {
                    val codigo = generarCodigo()
                    enviarCorreo(correoIngresado, codigo)
                    val intent = Intent(this, frmVerificaIdentidadActivity::class.java)
                    intent.putExtra("codigo", codigo)
                    intent.putExtra("correo", correoIngresado)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Correo no registrado en el sistema.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun buscarCorreo(correo: String, callback: (Boolean) -> Unit) {
        val dbMedicos = FirebaseDatabase.getInstance().getReference("medicos")
        val dbPacientes = FirebaseDatabase.getInstance().getReference("pacientes")

        var encontrado = false
        var consultasTerminadas = 0

        fun verificarFinConsulta() {
            consultasTerminadas++
            if (consultasTerminadas == 2) {
                callback(encontrado)
            }
        }

        dbMedicos.orderByChild("correo").equalTo(correo)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        encontrado = true
                    }
                    verificarFinConsulta()
                }

                override fun onCancelled(error: DatabaseError) {
                    verificarFinConsulta()
                }
            })

        dbPacientes.orderByChild("correo").equalTo(correo)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        encontrado = true
                    }
                    verificarFinConsulta()
                }
                override fun onCancelled(error: DatabaseError) {
                    verificarFinConsulta()
                }
            })
    }


    private fun enviarCorreo(correo: String, codigo: String) {
        val emailSender = EmailSender("cesarin7814@gmail.com", "chicharo7878")
        val codigo = generarCodigo()
        emailSender.enviarCorreo(
            destino = correo,
            asunto = "Código de recuperación",
            mensaje = "Ingresa este código de recuperación:\n\n$codigo"
        )
    }

    private fun generarCodigo(): String {
        val chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        return (1..6).map { chars.random() }.joinToString("")
    }
}
