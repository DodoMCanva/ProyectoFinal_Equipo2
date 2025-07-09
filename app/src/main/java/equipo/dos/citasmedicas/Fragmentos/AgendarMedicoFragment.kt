package equipo.dos.citasmedicas.Fragmentos

import Persistencia.ConfiguracionHorario
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
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

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
    private lateinit var btnConfirmar: Button
    private lateinit var tvEtiqueta: TextView

    var m: medico? = null
    var id: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvCosto = view.findViewById<TextView>(R.id.tvMontoAgendar)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelar)
        val txtMotivo = view.findViewById<EditText>(R.id.txtMotivo)
        tvEtiqueta = view.findViewById(R.id.etiquetaHora)
        btnConfirmar = view.findViewById(R.id.btnConfirmar)
        spHora = view.findViewById(R.id.spHora)
        tvFecha = view.findViewById(R.id.tvAgendarFecha)
        tvHoraSeleccionada = view.findViewById(R.id.tvHoraSeleccionada)

        m = arguments?.getSerializable("medico") as? medico

        id = m?.uid
        if (id == null) {
            m = sesion.guardadoEmergente as? medico
            id = m?.uid
        }
        if (m != null) {
            sesion.asignarGuardado(m!!)
        }
        view.findViewById<TextView>(R.id.tvAgendarNombre).setText(m?.nombre)

        deshbilitarHoras()

        Toast.makeText(
            requireContext(),
            "Seleccione una fecha primero",
            Toast.LENGTH_SHORT
        ).show()

        modulo.obtenerConfiguracionDelMedico(id!!) { config ->
            tvCosto.setText(config?.costoCita.toString() ?: "Horario no Configurado")
            view.findViewById<ImageButton>(R.id.btnCalendario).setOnClickListener {
                if (config != null) {
                    deshbilitarHoras()
                    mostrarSelectorFecha(config)
                }
            }

            spHora.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    tvHoraSeleccionada.text = parent.getItemAtPosition(pos).toString()
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }


            btnConfirmar.setOnClickListener {
                val fechaStr = tvFecha.text.toString()
                val horaStr = spHora.selectedItem?.toString() ?: ""
                val motivoStr = txtMotivo.text.toString().trim()
                val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val fechaActual = formato.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                val fechaConsulta = formato.parse(fechaStr)
                if (fechaConsulta.before(fechaActual)) {
                    return@setOnClickListener
                }

                if (fechaStr.isBlank() || horaStr.isBlank() || motivoStr.isBlank()) {
                    Toast.makeText(
                        requireContext(),
                        "Completa todos los campos",
                        Toast.LENGTH_SHORT
                    ).show()
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
                        val paciente =
                            sesion.obtenerSesion() as? paciente ?: return@setOnClickListener

                        modulo.validarDiaConsulta(med.uid!!, fechaStr, horaStr) { válido ->
                            if (!isAdded) return@validarDiaConsulta

                            if (válido) {
                                val db = FirebaseDatabase.getInstance().getReference("usuarios")
                                    .child("citas")
                                val citaId = db.push().key
                                if (citaId == null) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Error generando ID",
                                        Toast.LENGTH_SHORT
                                    ).show()
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
                                            Toast.makeText(
                                                requireContext(),
                                                "Cita agendada con éxito.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            parentFragmentManager.beginTransaction()
                                                .replace(
                                                    R.id.contenedorFragmento,
                                                    CitasFragment()
                                                )
                                                .addToBackStack(null)
                                                .commit()
                                        }
                                        dialog.dismiss()
                                    }
                                    .addOnFailureListener {
                                        if (isAdded) {
                                            Toast.makeText(
                                                requireContext(),
                                                "Error al agendar: ${it.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        dialog.dismiss()
                                    }
                            } else {
                                if (isAdded) Toast.makeText(
                                    requireContext(),
                                    "Hora no disponible. Elige otra.",
                                    Toast.LENGTH_SHORT
                                ).show()
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
    }


    private fun mostrarSelectorFecha(config: ConfiguracionHorario) {
        val calendar = Calendar.getInstance()
        val y = calendar.get(Calendar.YEAR)
        val m = calendar.get(Calendar.MONTH)
        val d = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                if (modulo.listaInhabiles(config).contains(dayOfWeek)) {
                    Toast.makeText(
                        requireContext(),
                        "Este día no está disponible",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val fechaStr = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    tvFecha.text = fechaStr
                    configurarSpinnerHoras(year, month, dayOfMonth, config)
                }
            },
            y, m, d
        )
        datePickerDialog.show()
    }


    private fun configurarSpinnerHoras(
        year: Int,
        month: Int,
        day: Int,
        config: ConfiguracionHorario
    ) {
        val cal = Calendar.getInstance().apply { set(year, month, day) }
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        val fecha = String.format("%02d/%02d/%04d", day, month + 1, year)

        if (config == null) {
            Toast.makeText(context, "No tiene Horarios Disponibles", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val (mañanaP, tardeP) = modulo.obtenerConfigDelDia(config, dow)
        val horas = mutableListOf<String>()
        if (mañanaP.first) horas += modulo.generarHorasEnHorario(
            mañanaP.second.desde,
            mañanaP.second.hasta,
            config.duracionConsulta
        )
        if (tardeP.first) horas += modulo.generarHorasEnHorario(
            tardeP.second.desde,
            tardeP.second.hasta,
            config.duracionConsulta
        )

        if (horas.isEmpty()) {
            Toast.makeText(context, "No hay horarios disponibles", Toast.LENGTH_SHORT)
                .show()
            spHora.adapter = null
        } else {
            modulo.eliminarHorasOcupadas(this.id!!, fecha, horas) { horasRestantes ->
                if (horasRestantes.isEmpty()) {
                    Toast.makeText(
                        context,
                        "No hay horarios disponibles",
                        Toast.LENGTH_SHORT
                    ).show()
                    spHora.adapter = null
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Selecciona la hora",
                        Toast.LENGTH_SHORT
                    ).show()
                    habilitarHoras()
                    spHora.adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        horasRestantes
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                }
            }
        }
    }

    fun habilitarHoras(){
        tvHoraSeleccionada.visibility = View.VISIBLE
        tvEtiqueta.visibility = View.VISIBLE
        spHora.visibility = View.VISIBLE
        btnConfirmar.isEnabled = true
        spHora.isEnabled = true
    }

    fun deshbilitarHoras(){
        spHora.visibility = View.INVISIBLE
        tvEtiqueta.visibility = View.INVISIBLE
        tvHoraSeleccionada.visibility = View.INVISIBLE
        btnConfirmar.isEnabled = false
        spHora.isEnabled = false
    }

    override fun onResume() {
        super.onResume()
        val tvEncabezado: TextView? =
            (activity as? frmPrincipalActivity)?.findViewById(R.id.encabezadoPrincipal)
        tvEncabezado?.text = "Agendar"
    }
}
