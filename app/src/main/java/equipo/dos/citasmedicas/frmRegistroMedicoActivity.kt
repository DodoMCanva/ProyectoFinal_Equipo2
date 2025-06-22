package equipo.dos.citasmedicas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class frmRegistroMedicoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_registro_medico)


        val btnRegistrarseMedico : Button = findViewById(R.id.btnRegistrarseMedico)


        var intent : Intent

        btnRegistrarseMedico.setOnClickListener {
            intent = Intent(this, frmPrincipalActivity::class.java)
            intent.putExtra("tipoUsuario", "doctor")
            startActivity(intent)
        }

    }
}