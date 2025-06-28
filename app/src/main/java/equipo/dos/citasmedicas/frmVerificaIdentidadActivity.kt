package equipo.dos.citasmedicas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class frmVerificaIdentidadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_verifica_identidad)

        val etCodigo = findViewById<EditText>(R.id.et_codigoVerificacion)
        val btnVerificar = findViewById<Button>(R.id.btnVerificar)

        val codigo = intent.getStringExtra("codigo")
        val correo = intent.getStringExtra("correo")

        btnVerificar.setOnClickListener {
            val codigoIngresado = etCodigo.text.toString().uppercase()
            if (codigoIngresado == codigo) {
                val intent = Intent(this, frmNuevaContrasenaActivity::class.java)
                intent.putExtra("correo", correo)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Código incorrecto, intenta de nuevo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}