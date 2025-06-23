package equipo.dos.citasmedicas

import Persistencia.medico
import Persistencia.paciente
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import equipo.dos.citasmedicas.databinding.ActivityFrmEditarBinding
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding

class frmEditarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frm_editar)
// Obtener referencia de todos los campos
        val etNombre = findViewById<EditText>(R.id.etEditarNombre)
        val tvCorreo = findViewById<TextView>(R.id.tvEditarCorreo)
        val tvFecha = findViewById<TextView>(R.id.tvFechaRegistroRegistro)
        val etTelefono = findViewById<EditText>(R.id.etEditarTelefono)
        val cbHombre = findViewById<CheckBox>(R.id.cbEditarHombre)
        val cbMujer = findViewById<CheckBox>(R.id.cbEditarMujer)

        // Campos exclusivos del médico
        val seccionMedico = findViewById<LinearLayout>(R.id.editarPerfilMedicoSeccion)
        val spEspecialidad = findViewById<Spinner>(R.id.spEditarEspecialidad)
        val etCedula = findViewById<EditText>(R.id.etEditarCedula)
        val etEstado = findViewById<EditText>(R.id.etEditarEstado)
        val etCalle = findViewById<EditText>(R.id.etEditarCalle)
        val etNumero = findViewById<EditText>(R.id.etEditarNumero)
        val etCP = findViewById<EditText>(R.id.etEditarCodigoPostal)

        // Recuperar objeto recibido
        val sesion = Persistencia.sesion.sesion

        // Identificar tipo
        when (sesion) {
            is medico -> {
                val m = sesion

                etNombre.setText(m.nombre)
                tvCorreo.text = m.correo
                tvFecha.text = m.fechaNacimiento
                etTelefono.setText(m.telefono)

                if (m.genero.equals("Hombre", ignoreCase = true)) cbHombre.isChecked = true
                else if (m.genero.equals("Mujer", ignoreCase = true)) cbMujer.isChecked = true

                seccionMedico.visibility = LinearLayout.VISIBLE

                val especialidades = listOf(
                    "Cardiología", "Pediatría", "Dermatología", "Ginecología",
                    "Neurología", "Psiquiatría", "Oftalmología", "Medicina General"
                )

                val adapterEspecialidades = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    especialidades
                )
                adapterEspecialidades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spEspecialidad.adapter = adapterEspecialidades

                val posicion = adapterEspecialidades.getPosition(m.especialidad)
                spEspecialidad.setSelection(posicion)

                etCedula.setText(m.cedula)
                etEstado.setText(m.estado)
                etCalle.setText(m.calle)
                etNumero.setText(m.numero)
                etCP.setText(m.cp)
            }

            is paciente -> {
                val p = sesion

                etNombre.setText(p.nombre)
                tvCorreo.text = p.correo
                tvFecha.text = p.fechaNacimiento
                etTelefono.setText(p.telefono)

                if (p.genero.equals("Hombre", ignoreCase = true)) cbHombre.isChecked = true
                else if (p.genero.equals("Mujer", ignoreCase = true)) cbMujer.isChecked = true

                seccionMedico.visibility = LinearLayout.GONE
            }



            else -> {
                Toast.makeText(this, "No se pudo cargar la sesión", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        // Botón cancelar (volver)
        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            finish()
        }

        // Botón guardar
        findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            // Aquí colocas la lógica para guardar cambios (ya sea en base de datos o en memoria temporal)
            Toast.makeText(this, "Cambios guardados (falta implementar lógica)", Toast.LENGTH_SHORT).show()
        }
    }
}