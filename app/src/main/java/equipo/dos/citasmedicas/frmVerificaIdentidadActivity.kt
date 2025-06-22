package equipo.dos.citasmedicas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class frmVerificaIdentidadActivity : AppCompatActivity() {

    //código correcto es "123456" por ahora
    val codigoCorrecto = "123456"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_verifica_identidad)

        val etCodigo = findViewById<EditText>(R.id.et_codigoVerificacion)
        val btnVerificar = findViewById<Button>(R.id.btnVerificar)

        btnVerificar.setOnClickListener {
            val codigoIngresado = etCodigo.text.toString().trim()

            if (codigoIngresado == codigoCorrecto) {
                val intent = Intent(this, frmNuevaContrasenaActivity::class.java)
                // Puedes pasar datos extra si los necesitas
                startActivity(intent)
            } else {
                Toast.makeText(this, "Código incorrecto, intenta de nuevo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}