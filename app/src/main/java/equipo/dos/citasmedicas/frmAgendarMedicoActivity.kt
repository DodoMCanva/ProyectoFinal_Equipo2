package equipo.dos.citasmedicas

import Persistencia.medico
import Persistencia.paciente
import android.app.DatePickerDialog
import android.app.Dialog
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import Persistencia.sesion
import Persistencia.cita
import java.util.UUID
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding
import equipo.dos.citasmedicas.helpers.MenuDesplegable
import java.util.Calendar

class frmAgendarMedicoActivity : AppCompatActivity() {


    private val binding by lazy {
        ActivityFrmPrincipalBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frm_agendar_medico)
        MenuDesplegable.configurarMenu(this)

        //calendario
        val btnCalendario = findViewById<ImageButton>(R.id.btnCalendario)
        val tvFecha = findViewById<TextView>(R.id.tvAgendarFecha)
        val tvHoraSeleccionada = findViewById<TextView>(R.id.tvHoraSeleccionada)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        btnCalendario.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker =
                DatePickerDialog(this, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    val fechaSeleccionada =
                        String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    tvFecha.text = fechaSeleccionada
                }, anio, mes, dia)

            datePicker.show()
        }

        //spinner hora
        val spHora = findViewById<Spinner>(R.id.spHora)
        fun generarHorasCada30Min(): List<String> {
            val horas = mutableListOf<String>()
            var hora = 7
            var minuto = 0

            while (hora < 19 || (hora == 19 && minuto == 0)) {
                val amPm = if (hora < 12) "AM" else "PM"
                val hora12 = if (hora % 12 == 0) 12 else hora % 12
                val horaFormateada = String.format("%d:%02d %s", hora12, minuto, amPm)
                horas.add(horaFormateada)

                minuto += 30
                if (minuto >= 60) {
                    minuto = 0
                    hora++
                }
            }

            return horas
        }

        val horasDisponibles = generarHorasCada30Min()

        val adapterHoras =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, horasDisponibles)
        adapterHoras.setDropDownViewResource(R.layout.spinner_item_custom_hora)
        spHora.adapter = adapterHoras

        spHora.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                val horaSeleccionada = parent.getItemAtPosition(position).toString()
                tvHoraSeleccionada.text = horaSeleccionada
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {

            }
        }

        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        val txtMotivo = findViewById<EditText>(R.id.txtMotivo)

        btnConfirmar.setOnClickListener {
            val fecha = tvFecha.text.toString()
            val hora = spHora.selectedItem.toString()
            val motivo = txtMotivo.text.toString()

            if (motivo.isEmpty()) {
                Toast.makeText(this, "Escribe un motivo para la cita", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //mostrar el diálogo
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_confirmacion_cita)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val btnAceptar = dialog.findViewById<Button>(R.id.btnConfirmarCancelacion)

            btnAceptar.setOnClickListener {
                val medicoSeleccionado = intent.getSerializableExtra("medico") as? medico
                val pacienteActual = sesion.obtenerSesion() as? paciente

                if (medicoSeleccionado == null || pacienteActual == null) {
                    Toast.makeText(
                        this,
                        "Error: No se pudo obtener la información de usuario.",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                    return@setOnClickListener
                }

                val database = FirebaseDatabase.getInstance().getReference("usuarios").child("citas")
                val citaId = database.push().key

                if (citaId == null) {
                    Toast.makeText(this, "Error al generar el ID de la cita.", Toast.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                    return@setOnClickListener
                }

                val nuevaCita = cita(
                    idCita = citaId,
                    idMedico = medicoSeleccionado.uid,
                    idPaciente = (sesion.obtenerSesion() as paciente).uid,
                    nombreMedico = medicoSeleccionado.nombre,
                    nombrePaciente = pacienteActual.nombre,
                    fecha = fecha,
                    hora = hora,
                    motivo = motivo,
                    estado = "Pendiente",
                    especialidad = medicoSeleccionado.especialidad,
                    imagenMedico = medicoSeleccionado.fotoPerfil,
                    imagenPaciente = pacienteActual.fotoPerfil
                )

                // Guarda la cita
                database.child(citaId).setValue(nuevaCita)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Cita agendada con éxito.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, frmPrincipalActivity::class.java)
                        startActivity(intent)
                        finish()
                        dialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Error al agendar la cita: ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                    }

            }
            dialog.show()

        }


        btnCancelar.setOnClickListener {
            {
                intent = Intent(this, frmAgendarActivity::class.java)
                startActivity(intent)
            }

            val medico = intent.getSerializableExtra("medico") as? medico

            if (medico != null) {
                val tvNombre = findViewById<TextView>(R.id.tvAgendarNombre)
                val tvMonto = findViewById<TextView>(R.id.tvMonto)
                tvNombre.text = medico.nombre
                tvMonto.text = "$${medico.costoConsulta}"
            } else {
                Toast.makeText(this, "Médico no recibido", Toast.LENGTH_SHORT).show()
                finish()
            }

            MenuDesplegable.configurarMenu(this)
        }
    }
}
