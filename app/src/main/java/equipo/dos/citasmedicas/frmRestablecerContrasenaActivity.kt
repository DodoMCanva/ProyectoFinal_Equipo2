package equipo.dos.citasmedicas

import Persistencia.fakebd
import Persistencia.medico
import Persistencia.paciente
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class frmRestablecerContrasenaActivity : AppCompatActivity() {
    //se va usar poco
    //var bd : fakebd =

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_restablecer_contrasena)

        val etCorreo = findViewById<EditText>(R.id.et_correoResContra)
        val btnEnviarCodigo = findViewById<Button>(R.id.btnEnviarCodigo)

        btnEnviarCodigo.setOnClickListener {
            val intent = Intent(this, frmVerificaIdentidadActivity::class.java)
            if (buscarCorreo(etCorreo.text.toString())){
                intent.putExtra("correo", etCorreo.text.toString()) // ahora sí existe
                startActivity(intent)
            }
        }

    }

    fun buscarCorreo( correo : String):Boolean{
        //
        var listaPacientes : ArrayList<paciente> = ArrayList<paciente>()
        var listaMedicos : ArrayList<medico> = ArrayList<medico>()
        val paciente = listaPacientes.find { it.correo == correo }
        val medico = listaMedicos.find { it.correo == correo }
        if (paciente != null){
            return true
        }
        if (medico != null){
            return true
        }
        return false
    }

}