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
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)


class CitasFragment : Fragment() {
    var adapter: AdapterCita? = null
    var filtroBusqueda : Boolean = false

    //NOTA: formato anterior yyyy-MM-dd
    var fechaBusqueda: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    var fechaFinale : String = LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

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

        fechaTexto.setText(fechaBusqueda)
        fechaInicio.setText(fechaBusqueda)
        fechaFinal.setText(fechaFinale)
        fechaInicio.visibility = View.INVISIBLE
        fechaFinal.visibility = View.INVISIBLE

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

            val datePicker = DatePickerDialog(requireContext(), { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val fechaSeleccionadaCal = Calendar.getInstance()
                fechaSeleccionadaCal.set(year, month, dayOfMonth)
                val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val fechaSeleccionadaStr = formato.format(fechaSeleccionadaCal.time)
                fechaTexto.text = fechaSeleccionadaStr
                fechaBusqueda = fechaSeleccionadaStr
                if (filtroBusqueda) {
                    fechaInicio.visibility = View.VISIBLE
                    fechaFinal.visibility = View.VISIBLE
                    fechaInicio.setText(fechaBusqueda)
                    fechaSeleccionadaCal.add(Calendar.DAY_OF_MONTH, 7)
                    fechaFinale = formato.format(fechaSeleccionadaCal.time)
                    fechaFinal.setText(fechaFinale)
                } else {
                    fechaInicio.visibility = View.INVISIBLE
                    fechaFinal.visibility = View.INVISIBLE
                    fechaInicio.setText(fechaBusqueda)
                    fechaSeleccionadaCal.add(Calendar.DAY_OF_MONTH, 7)
                    fechaFinale = formato.format(fechaSeleccionadaCal.time)
                    fechaFinal.setText(fechaFinale)
                }
                adaptarCitas(listaCitas)
            }, anio, mes, dia)
            datePicker.show()
        }

        filtro.setOnCheckedChangeListener { _, isChecked ->
            filtroBusqueda = isChecked
            if (filtroBusqueda) {
                fechaInicio.visibility = View.VISIBLE
                fechaFinal.visibility = View.VISIBLE
            } else {
                fechaInicio.visibility = View.INVISIBLE
                fechaFinal.visibility = View.INVISIBLE
            }
            adaptarCitas(listaCitas)
        }

    }

    fun adaptarCitas(listView: ListView) {
        val listaCitas: ListView = listView
        sesion.actualizarListaCitas {
            if (sesion.citas != null && sesion.citas.isNotEmpty()) {
                var lista = ArrayList<cita>()
                if (filtroBusqueda) {
                    lista = sesion.listaOrdenada().semana(fechaBusqueda, fechaFinale)
                }else{
                    lista = sesion.listaOrdenada().dia(fechaBusqueda)
                }
                adapter = AdapterCita(requireContext(), lista, sesion.tipo, filtroBusqueda){ citaSeleccionada ->
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


    fun ArrayList<cita>.semana(inicio: String, fin: String): ArrayList<cita> {
        val lista = ArrayList<cita>()
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())  // Corregí el patrón de la fecha

        val fechaInicio = formato.parse(inicio)
        val fechaFin = formato.parse(fin)

        var fechaActual = fechaInicio
        while (fechaActual.before(fechaFin) || fechaActual.equals(fechaFin)) {
            lista.add(cita(idCita = "encabezado", fecha = formato.format(fechaActual.time)))
            for (cita in this) {
                val fechaCita = formato.parse(cita.fecha)
                if (fechaCita.equals(fechaActual)) {
                    lista.add(cita)
                }
            }
            fechaActual = Calendar.getInstance().apply {
                time = fechaActual
                add(Calendar.DAY_OF_MONTH, 1)
            }.time
        }
        return lista
    }


    fun ArrayList<cita>.dia(fecha : String) : ArrayList<cita>{
        val lista = ArrayList<cita>()
        val formato = SimpleDateFormat("dd/MM/yyyyy", Locale.getDefault())
        val fecha = formato.parse(fecha)
        for (cita in this){
            val fechaCita = formato.parse(cita.fecha)
            if (fechaCita == fecha ) {
                lista.add(cita)
            }
        }
        return lista
    }


}