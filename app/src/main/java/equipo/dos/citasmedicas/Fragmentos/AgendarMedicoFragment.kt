package equipo.dos.citasmedicas.Fragmentos

import Persistencia.cita
import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.database.FirebaseDatabase
import equipo.dos.citasmedicas.R
import equipo.dos.citasmedicas.frmPrincipalActivity
import java.util.Calendar

class AgendarMedicoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_agendar_medico, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //calendario
        val btnCalendario = view.findViewById<ImageButton>(R.id.btnCalendario)
        val tvNombreMedico = view.findViewById<TextView>(R.id.tvAgendarNombre)
        val tvMonto = view.findViewById<TextView>(R.id.tvMontoAgendar)
        val tvFecha = view.findViewById<TextView>(R.id.tvAgendarFecha)
        val tvHoraSeleccionada = view.findViewById<TextView>(R.id.tvHoraSeleccionada)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelar)

        tvNombreMedico.setText((arguments?.getSerializable("medico") as? medico)?.nombre)

        tvMonto.setText((arguments?.getSerializable("medico") as? medico)?.costoConsulta.toString())

        btnCalendario.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker =
                DatePickerDialog(
                    requireContext(),
                    { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                        val fechaSeleccionada =
                            String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                        tvFecha.text = fechaSeleccionada
                    },
                    anio,
                    mes,
                    dia
                )

            datePicker.show()
        }

        //spinner hora
        val spHora = view.findViewById<Spinner>(R.id.spHora)
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
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, horasDisponibles)
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

        val btnConfirmar = view.findViewById<Button>(R.id.btnConfirmar)
        val txtMotivo = view.findViewById<EditText>(R.id.txtMotivo)

        btnConfirmar.setOnClickListener {
            val fecha = tvFecha.text.toString()
            val hora = spHora.selectedItem.toString()
            val motivo = txtMotivo.text.toString()

            if (motivo.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Escribe un motivo para la cita",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_confirmacion_cita)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val btnAceptar = dialog.findViewById<Button>(R.id.btnConfirmarCancelacion)

            btnAceptar.setOnClickListener {
                val medicoSeleccionado = arguments?.getSerializable("medico") as? medico
                val pacienteActual = sesion.obtenerSesion() as? paciente

                if (medicoSeleccionado == null || pacienteActual == null) {
                    Toast.makeText(
                        requireContext(),
                        "Error: No se pudo obtener la información de usuario.",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                    return@setOnClickListener
                }

                val database =
                    FirebaseDatabase.getInstance().getReference("usuarios").child("citas")
                val citaId = database.push().key

                if (citaId == null) {
                    Toast.makeText(
                        requireContext(),
                        "Error al generar el ID de la cita.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    dialog.dismiss()
                    return@setOnClickListener
                }

                val nuevaCita = cita(
                    idCita = citaId,
                    idMedico = medicoSeleccionado.uid,
                    idPaciente = sesion.uid,
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

                database.child(citaId).setValue(nuevaCita)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Cita agendada con éxito.",
                            Toast.LENGTH_SHORT
                        ).show()

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.contenedorFragmento, CitasFragment())
                            .addToBackStack(null)
                            .commit()

                        dialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Error al agendar la cita: ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                    }

            }
            dialog.show()

        }


        btnCancelar.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragmento, CitasFragment())
                .addToBackStack(null)
                .commit()
        }

    }
    override fun onResume() {
        super.onResume()
        val tvEncabezado: TextView? = (activity as? frmPrincipalActivity)?.findViewById(R.id.encabezadoPrincipal)
        tvEncabezado?.text = "Agendar"
    }
}