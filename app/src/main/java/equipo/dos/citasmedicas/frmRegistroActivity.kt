package equipo.dos.citasmedicas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class frmRegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_registro)


        val btnRegistrarse : Button = findViewById(R.id.btnRegistrarse)


        var intent : Intent

        btnRegistrarse.setOnClickListener {
            intent = Intent(this, frmPrincipalActivity::class.java)
            intent.putExtra("tipoUsuario", "paciente")
            startActivity(intent)
        }


    }
}