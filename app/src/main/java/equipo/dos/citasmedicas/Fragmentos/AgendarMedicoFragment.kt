package equipo.dos.citasmedicas.Fragmentos

import Persistencia.medico
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
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
    private lateinit var tvNombreMedico: TextView
    private lateinit var tvCosto: TextView
    var m : medico? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        m = arguments?.getSerializable("medico") as? medico
        spHora = view.findViewById(R.id.spHora)
        tvFecha = view.findViewById(R.id.tvAgendarFecha)
        tvHoraSeleccionada = view.findViewById(R.id.tvHoraSeleccionada)
        tvNombreMedico  = view.findViewById(R.id.tvAgendarNombre)
        tvCosto = view.findViewById(R.id.tvMontoAgendar)

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
                    Toast.makeText(context, "Error al cargar configuración", Toast.LENGTH_SHORT).show()
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