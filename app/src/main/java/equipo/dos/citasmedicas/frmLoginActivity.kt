package equipo.dos.citasmedicas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class frmLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_login)

        val btnIniciar : Button = findViewById(R.id.btnIniciarSesion)
        val btnRegistrarse : TextView = findViewById(R.id.tvRegistrarse)
        val btnCntr : TextView = findViewById(R.id.tvOlvidasteContra)

        var intent : Intent

        btnIniciar.setOnClickListener {
            //Aqui va la logica de inicio
            //buscar
            //intent = Intent(this, frmPrincipal::class.java)
            //put extra de el tipo de sesion
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
}