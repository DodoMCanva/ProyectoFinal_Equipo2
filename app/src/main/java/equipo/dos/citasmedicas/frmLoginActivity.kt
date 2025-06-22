package equipo.dos.citasmedicas

import Persistencia.fakebd
import Persistencia.paciente
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class frmLoginActivity : AppCompatActivity() {
    var bd : fakebd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_login)
        val correo : EditText = findViewById(R.id.et_correo)
        val contrasena : EditText = findViewById(R.id.et_contrasena)

        val btnIniciar : Button = findViewById(R.id.btnIniciarSesion)
        val btnRegistrarse : TextView = findViewById(R.id.tvRegistrarse)
        val btnCntr : TextView = findViewById(R.id.tvOlvidasteContra)

        var intent : Intent

        btnIniciar.setOnClickListener {
            intent = Intent(this, frmPrincipalActivity::class.java)

            if (auntenticarPaciente(correo.text.toString(), contrasena.text.toString()) != null){
                intent.putExtra("sesion", auntenticarPaciente(correo.text.toString(), contrasena.text.toString()))
                startActivity(intent)
            }




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
    fun llenarBD(){

    }

    fun auntenticarPaciente(correo : String,contrasena : String ): paciente?{
        var lista : ArrayList<paciente> = ArrayList<paciente>()
        for (p in lista){
            if (p.correo == correo && p.contrasena == contrasena)
            return p
        }
        return null
    }
}