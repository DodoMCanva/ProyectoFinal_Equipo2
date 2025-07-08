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
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import equipo.dos.citasmedicas.R
import equipo.dos.citasmedicas.frmPrincipalActivity
import modulos.AdapterCitaRecycler
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)


class CitasFragment : Fragment() {


    var adapter: AdapterCitaRecycler? = null
    var filtroSemana: Boolean = false
    var filtroDia: Boolean = false

    //NOTA: formato anterior yyyy-MM-dd
    var fechaBusqueda: String =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    var fechaFinale: String =
        LocalDateTime.now().plusDays(6).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_principal, container, false)

    }
    lateinit var listaCitas: RecyclerView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val semana: Switch = view.findViewById(R.id.swSemana)
        val dia: Switch = view.findViewById(R.id.swDia)
        val calendario: ImageButton = view.findViewById(R.id.btnCalendarioConsultaCitas)
        val fechaTexto: TextView = view.findViewById(R.id.tvConsultaFecha)
        val fechaInicio: TextView = view.findViewById(R.id.tvFechaInicio)
        val fechaFinal: TextView = view.findViewById(R.id.tvFechaFinal)
        listaCitas = view.findViewById(R.id.rvCitas)
        val btnAgendar: FloatingActionButton? = view.findViewById(R.id.btnAgendar)

        fechaTexto.setText(fechaBusqueda)
        fechaInicio.setText(fechaBusqueda)
        fechaFinal.setText(fechaFinale)
        fechaInicio.visibility = View.INVISIBLE
        fechaFinal.visibility = View.INVISIBLE
        this.listaCitas.layoutManager = LinearLayoutManager(requireContext())

        adaptarCitas(this.listaCitas)

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

            val datePicker = DatePickerDialog(
                requireContext(),
                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    val fechaSeleccionadaCal = Calendar.getInstance()
                    fechaSeleccionadaCal.set(year, month, dayOfMonth)
                    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val fechaSeleccionadaStr = formato.format(fechaSeleccionadaCal.time)
                    fechaTexto.text = fechaSeleccionadaStr
                    fechaBusqueda = fechaSeleccionadaStr
                    if (filtroSemana) {
                        fechaInicio.visibility = View.VISIBLE
                        fechaFinal.visibility = View.VISIBLE
                        fechaInicio.setText(fechaBusqueda)
                        fechaSeleccionadaCal.add(Calendar.DAY_OF_MONTH, 6)
                        fechaFinale = formato.format(fechaSeleccionadaCal.time)
                        fechaFinal.setText(fechaFinale)
                    } else {
                        fechaInicio.visibility = View.INVISIBLE
                        fechaFinal.visibility = View.INVISIBLE
                        fechaInicio.setText(fechaBusqueda)
                        fechaSeleccionadaCal.add(Calendar.DAY_OF_MONTH, 6)
                        fechaFinale = formato.format(fechaSeleccionadaCal.time)
                        fechaFinal.setText(fechaFinale)
                    }
                    adaptarCitas(listaCitas)
                },
                anio,
                mes,
                dia
            )
            datePicker.show()
        }

        semana.setOnCheckedChangeListener { _, isChecked ->
            filtroSemana = isChecked
            if (isChecked){
                dia.isChecked = false
            }
            if (filtroSemana) {
                fechaInicio.visibility = View.VISIBLE
                fechaFinal.visibility = View.VISIBLE
            } else {
                fechaInicio.visibility = View.INVISIBLE
                fechaFinal.visibility = View.INVISIBLE
            }
            adaptarCitas(listaCitas)
        }
        dia.setOnCheckedChangeListener { _, isChecked ->
            filtroDia = isChecked
            if (isChecked) {
                semana.isChecked = false
            }
            adaptarCitas(listaCitas)
        }

    }

    fun adaptarCitas(recyclerView: RecyclerView) {
        sesion.actualizarListaCitas {
            if (sesion.citas != null && sesion.citas.isNotEmpty()) {
                var lista: ArrayList<cita>

                lista = if (filtroDia) {
                    sesion.listaOrdenada().dia(fechaBusqueda)
                } else if (filtroSemana) {
                    sesion.listaOrdenada().semana(fechaBusqueda, fechaFinale)
                    .encabezar(fechaBusqueda, fechaFinale)
                } else {
                    sesion.listaOrdenada().actuales()
                }

                if (sesion.tipo == "paciente") {
                    lista = lista.eliminarCompletadas()
                }


                val ctx = context ?: return@actualizarListaCitas

                adapter = AdapterCitaRecycler(ctx, lista, sesion.tipo) { citaSeleccionada ->
                        val fragment = if (sesion.tipo == "paciente") {
                            DetalleCitaPacienteFragment()
                        } else {
                            DetalleCitaMedicoFragment()
                        }
                        val bundle = Bundle().apply { putString("citaId", citaSeleccionada.idCita) }
                        fragment.arguments = bundle
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.contenedorFragmento, fragment)
                            .addToBackStack(null)
                            .commit()
                    }
                recyclerView.adapter = adapter
            }
        }
    }


    fun ArrayList<cita>.semana(inicio: String, fin: String): ArrayList<cita> {
        val lista = ArrayList<cita>()
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaInicio = formato.parse(inicio)
        val fechaFin = formato.parse(fin)
        for (cita in this) {
            val fechaCita = formato.parse(cita.fecha)
            if (!fechaCita.before(fechaInicio) && !fechaCita.after(fechaFin)) {
                lista.add(cita)
            }
        }
        return lista
    }

    fun ArrayList<cita>.encabezar(inicio: String, fin: String): ArrayList<cita> {
        val lista = ArrayList<cita>()
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendario = Calendar.getInstance()
        calendario.time = formato.parse(inicio)
        val fechaFinDate = formato.parse(fin)
        while (!calendario.time.after(fechaFinDate)) {
            val fechaStr = formato.format(calendario.time)
            lista.add(cita(idCita = "encabezado", fecha = fechaStr))
            for (cita in this) {
                if (cita.fecha == fechaStr) {
                    lista.add(cita)
                }
            }
            calendario.add(Calendar.DAY_OF_MONTH, 1)
        }
        return lista

    }


    fun ArrayList<cita>.dia(fecha: String): ArrayList<cita> {
        val lista = ArrayList<cita>()
        val formato = SimpleDateFormat("dd/MM/yyyyy", Locale.getDefault())
        val fecha = formato.parse(fecha)
        for (cita in this) {
            val fechaCita = formato.parse(cita.fecha)
            if (fechaCita == fecha) {
                lista.add(cita)
            }
        }
        return lista
    }

    fun ArrayList<cita>.actuales(): ArrayList<cita> {
        val lista = ArrayList<cita>()
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaInicio =
            formato.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        for (cita in this) {
            val fechaCita = formato.parse(cita.fecha)
            if (!fechaCita.before(fechaInicio)) {
                lista.add(cita)
            }
        }
        return lista
    }

    fun ArrayList<cita>.eliminarCompletadas(): ArrayList<cita> {
        return ArrayList(this.filterNot { it.estado == "Completada" || it.estado == "Cancelada" })
    }

    override fun onResume() {
        super.onResume()
        val tvEncabezado: TextView? =
            (activity as? frmPrincipalActivity)?.findViewById(R.id.encabezadoPrincipal)
        tvEncabezado?.text = "Mis Citas"
        adaptarCitas(this.listaCitas)
    }
}