package equipo.dos.citasmedicas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class frmPerfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_perfil)

        val btnSi : Button = findViewById(R.id.btnSi)
        val btnNo : Button = findViewById(R.id.btnNo)

        var intent : Intent

        btnSi.setOnClickListener {
            intent = Intent(this, frmRegistroMedicoActivity::class.java)
            startActivity(intent)
        }
        btnNo.setOnClickListener {
            intent = Intent(this, frmRegistroActivity::class.java)
            startActivity(intent)
        }

    }
}