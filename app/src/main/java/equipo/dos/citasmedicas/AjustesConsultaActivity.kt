package equipo.dos.citasmedicas

import Persistencia.medico
import Persistencia.sesion
import Persistencia.paciente
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding
import equipo.dos.citasmedicas.helpers.MenuDesplegable

class AjustesConsultaActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityFrmPrincipalBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes_consulta)
        MenuDesplegable.configurarMenu(this)

        // Cambio de horario de consulta

        val ids = listOf(
            R.id.etLunesDeManana, R.id.etLunesAManana,
            R.id.etLunesDeTarde, R.id.etLunesATarde,
            R.id.etMartesDeManana, R.id.etMartesAManana,
            R.id.etMartesDeTarde, R.id.etMartesATarde,
            R.id.etMiercolesDeManana, R.id.etMiercolesAManana,
            R.id.etMiercolesDeTarde, R.id.etMiercolesATarde,
            R.id.etJuevesDeManana, R.id.etJuevesAManana,
            R.id.etJuevesDeTarde, R.id.etJuevesATarde,
            R.id.etViernesDeManana, R.id.etViernesAManana,
            R.id.etViernesDeTarde, R.id.etViernesATarde,
            R.id.etSabadoDeManana, R.id.etSabadoAManana,
            R.id.etSabadoDeTarde, R.id.etSabadoATarde,
            R.id.etDomingoDeManana, R.id.etDomingoAManana,
            R.id.etDomingoDeTarde, R.id.etDomingoATarde
        )

        for (id in ids) {
            val editText = findViewById<EditText>(id)
            editText.setOnClickListener {
                showTimePicker(editText)
            }
        }
        val btnGuardar = findViewById<Button>(R.id.btnGuardarConsulta)
        btnGuardar.setOnClickListener {
            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show()
        }

        // Lunes
        var lunesActivo = true
        val layoutLunes = findViewById<LinearLayout>(R.id.layoutLunes)
        val etLunesDeManana = findViewById<EditText>(R.id.etLunesDeManana)
        val etLunesAManana = findViewById<EditText>(R.id.etLunesAManana)
        val etLunesDeTarde = findViewById<EditText>(R.id.etLunesDeTarde)
        val etLunesATarde = findViewById<EditText>(R.id.etLunesATarde)

        layoutLunes.setOnClickListener {
            lunesActivo = !lunesActivo
            val alpha = if (lunesActivo) 1f else 0.4f
            val habilitado = lunesActivo
            etLunesDeManana.isEnabled = habilitado
            etLunesAManana.isEnabled = habilitado
            etLunesDeTarde.isEnabled = habilitado
            etLunesATarde.isEnabled = habilitado
            etLunesDeManana.alpha = alpha
            etLunesAManana.alpha = alpha
            etLunesDeTarde.alpha = alpha
            etLunesATarde.alpha = alpha
        }

        // Martes
        var martesActivo = true
        val layoutMartes = findViewById<LinearLayout>(R.id.layoutMartes)
        val etMartesDeManana = findViewById<EditText>(R.id.etMartesDeManana)
        val etMartesAManana = findViewById<EditText>(R.id.etMartesAManana)
        val etMartesDeTarde = findViewById<EditText>(R.id.etMartesDeTarde)
        val etMartesATarde = findViewById<EditText>(R.id.etMartesATarde)

        layoutMartes.setOnClickListener {
            martesActivo = !martesActivo
            val alpha = if (martesActivo) 1f else 0.4f
            val habilitado = martesActivo
            etMartesDeManana.isEnabled = habilitado
            etMartesAManana.isEnabled = habilitado
            etMartesDeTarde.isEnabled = habilitado
            etMartesATarde.isEnabled = habilitado
            etMartesDeManana.alpha = alpha
            etMartesAManana.alpha = alpha
            etMartesDeTarde.alpha = alpha
            etMartesATarde.alpha = alpha
        }

        // Miercoles
        var miercolesActivo = true
        val layoutMiercoles = findViewById<LinearLayout>(R.id.layoutMiercoles)
        val etMiercolesDeManana = findViewById<EditText>(R.id.etMiercolesDeManana)
        val etMiercolesAManana = findViewById<EditText>(R.id.etMiercolesAManana)
        val etMiercolesDeTarde = findViewById<EditText>(R.id.etMiercolesDeTarde)
        val etMiercolesATarde = findViewById<EditText>(R.id.etMiercolesATarde)

        layoutMiercoles.setOnClickListener {
            miercolesActivo = !miercolesActivo
            val alpha = if (miercolesActivo) 1f else 0.4f
            val habilitado = miercolesActivo
            etMiercolesDeManana.isEnabled = habilitado
            etMiercolesAManana.isEnabled = habilitado
            etMiercolesDeTarde.isEnabled = habilitado
            etMiercolesATarde.isEnabled = habilitado
            etMiercolesDeManana.alpha = alpha
            etMiercolesAManana.alpha = alpha
            etMiercolesDeTarde.alpha = alpha
            etMiercolesATarde.alpha = alpha
        }

        // Jueves

    }

    private fun showTimePicker(editText: EditText) {
        val cal = java.util.Calendar.getInstance()
        val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = cal.get(java.util.Calendar.MINUTE)

        val picker = android.app.TimePickerDialog(this, { _, h, m ->
            val hora = String.format("%02d:%02d", h, m)
            editText.setText(hora)
        }, hour, minute, true)

        picker.show()
    }

}
