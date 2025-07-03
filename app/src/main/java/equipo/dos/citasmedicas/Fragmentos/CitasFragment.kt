package equipo.dos.citasmedicas.Fragmentos

import Persistencia.cita
import Persistencia.sesion
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import equipo.dos.citasmedicas.R
import modulos.AdapterCita
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)


class CitasFragment : Fragment() {
    var adapter: AdapterCita? = null
    var filtroBusqueda : Boolean = false
    var fechaBusqueda : String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_principal, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val filtro: Switch = view.findViewById(R.id.swMostrarTodaSemana)
        val calendario: ImageButton = view.findViewById(R.id.btnCalendarioConsultaCitas)
        val fechaTexto: TextView = view.findViewById(R.id.tvConsultaFecha)
        val fechaInicio : TextView = view.findViewById(R.id.tvFechaInicio)
        val fechaFinal : TextView = view.findViewById(R.id.tvFechaFinal)
        val listaCitas: ListView = view.findViewById(R.id.lvCitas)
        val btnAgendar: FloatingActionButton? = view.findViewById(R.id.btnAgendar)

        adaptarCitas(listaCitas)

        if (sesion.tipo == "paciente") {
            btnAgendar?.visibility = View.VISIBLE
        } else {
            btnAgendar?.visibility = View.GONE

        }
        btnAgendar?.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragmento, AgendarFragment())
                .addToBackStack(null)
                .commit()
        }

        calendario.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker =
                DatePickerDialog(requireContext(), { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    val fechaSeleccionada =
                        String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    fechaTexto.text = fechaSeleccionada
                }, anio, mes, dia)
            datePicker.show()

            fechaBusqueda = fechaTexto.text.toString()
            if (filtroBusqueda){
                fechaInicio.visibility = View.VISIBLE
                fechaFinal.visibility = View.VISIBLE
                fechaInicio.setText(fechaBusqueda)
                //fechaFinal.setText()
            }else{
                fechaInicio.visibility = View.INVISIBLE
                fechaFinal.visibility = View.INVISIBLE
            }
            adaptarCitas(listaCitas)
        }

        filtro.setOnClickListener {
            filtroBusqueda = !filtroBusqueda
            adaptarCitas(listaCitas)

        }

    }

    fun adaptarCitas(listView: ListView) {
        val listaCitas: ListView = listView
        sesion.actualizarListaCitas {
            if (sesion.citas != null && sesion.citas.isNotEmpty()) {

                adapter = AdapterCita(requireContext(), sesion.citas, sesion.tipo, filtroBusqueda, fechaBusqueda){ citaSeleccionada ->
                    var fragment: Fragment
                    if (sesion.tipo == "paciente"){
                        fragment = DetalleCitaPacienteFragment()
                    }else{
                        fragment = DetalleCitaMedicoFragment()
                    }

                    val bundle = Bundle().apply {
                        putString("citaId", citaSeleccionada.idCita)
                    }
                    fragment.arguments = bundle

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.contenedorFragmento, fragment)
                        .addToBackStack(null)
                        .commit()
                }
                listaCitas.adapter = adapter
            }
        }
    }
}