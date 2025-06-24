package equipo.dos.citasmedicas

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class frmRegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_registro)

        val etNombre: EditText = findViewById(R.id.etNombreRegistro)
        val etCorreo: EditText = findViewById(R.id.etRegistroCorreo)
        val tvFecha: TextView = findViewById(R.id.tvFechaRegistroRegistro)
        val etContra: EditText = findViewById(R.id.etContraRegistro)
        val etConfContra: EditText = findViewById(R.id.etnuevaContraRegistro)
        val etTelefono: EditText = findViewById(R.id.etTelefonoRegistro)
        val cbHombre: CheckBox = findViewById(R.id.cbHombreRegistro)
        val cbMujer: CheckBox = findViewById(R.id.cbMujerRegistro)
        val btnRegistrarse: Button = findViewById(R.id.btnRegistrarse)
        val btnCalendario: ImageButton = findViewById(R.id.btnCalendarioRegistroPaciente)

        btnCalendario.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val selectorFecha = DatePickerDialog(this, { _, y, m, d ->
                val fechaFormateada = String.format("%02d/%02d/%04d", d, m + 1, y)
                tvFecha.text = fechaFormateada
            }, anio, mes, dia)

            selectorFecha.datePicker.maxDate = System.currentTimeMillis()
            selectorFecha.show()
        }

        //checkBox de genero
        cbHombre.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) cbMujer.isChecked = false
        }

        cbMujer.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) cbHombre.isChecked = false
        }

        btnRegistrarse.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val fecha = tvFecha.text.toString().trim()
            val contra = etContra.text.toString()
            val confContra = etConfContra.text.toString()
            val telefono = etTelefono.text.toString().trim()
            val genero = when {
                cbHombre.isChecked -> "Hombre"
                cbMujer.isChecked -> "Mujer"
                else -> ""
            }

            if (nombre.isEmpty() || correo.isEmpty() || fecha.isEmpty() ||
                contra.isEmpty() || confContra.isEmpty() || telefono.isEmpty() || genero.isEmpty()
            ) {
                Toast.makeText(this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
            if (!emailRegex.matches(correo)) {
                Toast.makeText(this, "Correo electrónico inválido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contra.length < 5) {
                Toast.makeText(this, "La contraseña debe tener al menos 5 caracteres.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contra != confContra) {
                Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val telefonoRegex = Regex("^\\d{10}$")
            if (!telefonoRegex.matches(telefono)) {
                Toast.makeText(this, "El número de teléfono debe tener 10 digitos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, frmLoginActivity::class.java)
            intent.putExtra("tipoUsuario", "paciente")
            startActivity(intent)
        }
    }
}