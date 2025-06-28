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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class frmRestablecerContrasenaActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_restablecer_contrasena)

        val etCorreo = findViewById<EditText>(R.id.et_correoResContra)
        val btnEnviarCodigo = findViewById<Button>(R.id.btnEnviarCodigo)

        dbRef = FirebaseDatabase.getInstance().getReference("usuarios")


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
        dbRef.orderByChild("correo").equalTo(correo)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }

    private fun enviarCorreo(correo: String, codigo: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(correo))
            putExtra(Intent.EXTRA_SUBJECT, "Código de recuperación")
            putExtra(Intent.EXTRA_TEXT, "Ingresa este código de recuperación:\n\n$codigo")
            setPackage("com.google.android.gm")
        }
        try {
            startActivity(Intent.createChooser(intent, "Enviar correo con..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, "No hay aplicaciones de correo instaladas.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generarCodigo(): String {
        val chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        return (1..6).map { chars.random() }.joinToString("")
    }
}
