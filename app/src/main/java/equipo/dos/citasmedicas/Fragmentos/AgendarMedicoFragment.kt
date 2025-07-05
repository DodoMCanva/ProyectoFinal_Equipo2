package equipo.dos.citasmedicas.Fragmentos

import Persistencia.cita
import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
import modulos.ModuloHorario
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
class AgendarMedicoFragment : Fragment() {
    val modulo = ModuloHorario()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_agendar_medico, container, false)
    }


    private lateinit var spHora: Spinner
    private lateinit var tvFecha: TextView
    private lateinit var tvHoraSeleccionada: TextView
    private lateinit var btnConfirmar : Button
    var m : medico? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvNombreMedico = view.findViewById<TextView>(R.id.tvAgendarNombre)
        val tvCosto = view.findViewById<TextView>(R.id.tvMontoAgendar)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelar)

        btnConfirmar = view.findViewById(R.id.btnConfirmar)
        m = arguments?.getSerializable("medico") as? medico
        spHora = view.findViewById(R.id.spHora)
        tvFecha = view.findViewById(R.id.tvAgendarFecha)
        tvHoraSeleccionada = view.findViewById(R.id.tvHoraSeleccionada)

        tvNombreMedico.setText(m?.nombre)
        tvCosto.setText(m?.costoConsulta.toString())

        view.findViewById<ImageButton>(R.id.btnCalendario).setOnClickListener {
            mostrarSelectorFecha()
        }

        spHora.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                tvHoraSeleccionada.text = parent.getItemAtPosition(pos).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        val txtMotivo = view.findViewById<EditText>(R.id.txtMotivo)

        btnConfirmar.setOnClickListener {
            val fechaStr = tvFecha.text.toString()
            val horaStr = spHora.selectedItem?.toString() ?: ""
            val motivoStr = txtMotivo.text.toString().trim()

            if (fechaStr.isBlank() || horaStr.isBlank() || motivoStr.isBlank()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dialog = Dialog(requireContext()).apply {
                setContentView(R.layout.dialog_confirmacion_cita)
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                window?.setLayout(
                    (resources.displayMetrics.widthPixels * 0.9).toInt(),
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            dialog.findViewById<Button>(R.id.btnConfirmarCancelacion)
                .setOnClickListener {

                    val med = m ?: return@setOnClickListener
                    val paciente = sesion.obtenerSesion() as? paciente ?: return@setOnClickListener

                    modulo.validarDiaConsulta(med.uid!!, fechaStr, horaStr) { válido ->
                        if (!isAdded) return@validarDiaConsulta

                        if (válido) {
                            val db = FirebaseDatabase.getInstance().getReference("usuarios").child("citas")
                            val citaId = db.push().key
                            if (citaId == null) {
                                Toast.makeText(requireContext(), "Error generando ID", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                                return@validarDiaConsulta
                            }

                            val nuevaCita = cita(
                                idCita = citaId,
                                idMedico = med.uid,
                                idPaciente = sesion.uid,
                                nombreMedico = med.nombre,
                                nombrePaciente = paciente.nombre,
                                fecha = fechaStr,
                                hora = horaStr,
                                motivo = motivoStr,
                                estado = "Pendiente",
                                especialidad = med.especialidad,
                                imagenMedico = med.fotoPerfil,
                                imagenPaciente = paciente.fotoPerfil
                            )

                            db.child(citaId).setValue(nuevaCita)
                                .addOnSuccessListener {
                                    if (isAdded) {
                                        Toast.makeText(requireContext(), "Cita agendada con éxito.", Toast.LENGTH_SHORT).show()
                                        parentFragmentManager.beginTransaction()
                                            .replace(R.id.contenedorFragmento, CitasFragment())
                                            .addToBackStack(null)
                                            .commit()
                                    }
                                    dialog.dismiss()
                                }
                                .addOnFailureListener {
                                    if (isAdded) {
                                        Toast.makeText(requireContext(), "Error al agendar: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                                    dialog.dismiss()
                                }
                        } else {
                            if (isAdded) Toast.makeText(requireContext(), "Hora no disponible. Elige otra.", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
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

    private fun mostrarSelectorFecha() {
        val cal = Calendar.getInstance()
        val datePicker = DatePickerDialog(requireContext(),
            { _: DatePicker, y: Int, m: Int, d: Int ->
                val fechaStr = String.format("%02d/%02d/%04d", d, m+1, y)
                tvFecha.text = fechaStr
                configurarSpinnerHoras(y, m, d)
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.minDate = cal.timeInMillis
        val maxDateCalendar = Calendar.getInstance()
        maxDateCalendar.add(Calendar.YEAR, 2)
        datePicker.datePicker.maxDate = maxDateCalendar.timeInMillis

        datePicker.show()

    }

    private fun configurarSpinnerHoras(year: Int, month: Int, day: Int) {
        val cal = Calendar.getInstance().apply { set(year, month, day) }
        val dow = cal.get(Calendar.DAY_OF_WEEK)

        val uid = m?.uid
        if (uid != null) {
            modulo.obtenerConfiguracionDelMedico(uid) { config ->
                if (config == null) {
                    btnConfirmar.isEnabled = false
                    spHora.isEnabled = false
                    Toast.makeText(context, "No tiene Horarios Disponibles", Toast.LENGTH_SHORT).show()
                    return@obtenerConfiguracionDelMedico
                }
                val (mañanaP, tardeP) = modulo.obtenerConfigDelDia(config, dow)
                val horas = mutableListOf<String>()
                if (mañanaP.first) horas += modulo.generarHorasEnHorario(mañanaP.second.desde, mañanaP.second.hasta)
                if (tardeP.first) horas += modulo.generarHorasEnHorario(tardeP.second.desde, tardeP.second.hasta)
                if (horas.isEmpty()) {
                    Toast.makeText(context, "No hay horarios disponibles", Toast.LENGTH_SHORT).show()
                    spHora.adapter = null
                } else {
                    spHora.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, horas).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                }
            }
        }

    }
    
    override fun onResume() {
        super.onResume()
        val tvEncabezado: TextView? = (activity as? frmPrincipalActivity)?.findViewById(R.id.encabezadoPrincipal)
        tvEncabezado?.text = "Agendar"
    }
}