package equipo.dos.citasmedicas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.functions.FirebaseFunctions
import android.util.Log

class frmNuevaContrasenaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_nueva_contrasena)

        val etNuevaContrasena = findViewById<EditText>(R.id.et_nuevaContrasena)
        val etConfirmarContrasena = findViewById<EditText>(R.id.et_confirmarContrasena)
        val btnCambiar = findViewById<Button>(R.id.btnCambiarContra)

        val correo = intent.getStringExtra("correo")?.trim() ?: ""

        Log.d("DEBUG_NUEVA_CONTRASENA", "Correo recibido: '$correo'")

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

            Log.d("DEBUG_CLICK", "Correo en click: '$correo', Nueva: '$nueva'")
            cambiar(correo, nueva)

        }
    }

    fun cambiar(correo: String, nueva: String) {
        val correoTrim = correo.trim()
        val nuevaTrim = nueva.trim()
        val functions = FirebaseFunctions.getInstance()
        val datos: Map<String, Any> = mapOf(
            "email" to correoTrim,
            "nuevaContrasena" to nuevaTrim
        )

        Log.d("DEBUG_CAMBIO", "Datos a enviar: $datos")

        functions
            .getHttpsCallable("cambiarContrasena")
            .call(datos)
            .addOnSuccessListener { result ->
                Log.d("DEBUG_CAMBIO", "Respuesta de Firebase: ${result.data}")
                val data = result.data as? Map<*, *>
                val success = data?.get("success") as? Boolean ?: false
                if (success) {
                    Toast.makeText(this, "Contraseña cambiada correctamente.", Toast.LENGTH_SHORT).show()
                    irAlLogin()
                } else {
                    val error = data?.get("error") as? String ?: "Error desconocido"
                    Log.e("DEBUG_CAMBIO", "Error desde función: $error")
                    Toast.makeText(this, "Error al cambiar la contraseña: $error", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("DEBUG_CAMBIO", "Excepción: ${e.message}", e)
                Toast.makeText(this, "Error al cambiar la contraseña: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun buscarEnPacientes(refPacientes: DatabaseReference, correo: String, nueva: String) {
        refPacientes.orderByChild("correo").equalTo(correo)
            .get()
            .addOnSuccessListener { snapshot2 ->
                if (snapshot2.exists()) {
                    val uid = snapshot2.children.first().key
                    refPacientes.child(uid!!).child("contrasena").setValue(nueva)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Contraseña actualizada correctamente (paciente).", Toast.LENGTH_SHORT).show()
                            irAlLogin()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Fallo al guardar en pacientes: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(this, "Paciente no encontrado con ese correo.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al consultar pacientes: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    fun irAlLogin() {
        val intent = Intent(this, frmLoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}

