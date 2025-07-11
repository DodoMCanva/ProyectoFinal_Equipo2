package equipo.dos.citasmedicas.Fragmentos

import Persistencia.cita
import Persistencia.sesion
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import equipo.dos.citasmedicas.R
import equipo.dos.citasmedicas.frmPrincipalActivity
import modulos.AdapterHistorial
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class HistorialFragment : Fragment() {
    private var adapter: AdapterHistorial? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_historial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvCitas = view.findViewById<RecyclerView>(R.id.rvCitas)
        rvCitas.layoutManager = LinearLayoutManager(requireContext())

        adapter = AdapterHistorial(requireContext(), listOf(), sesion.tipo) { citaSeleccionada ->
            val fragment = if (sesion.tipo == "paciente") {
                DetalleCitaPacienteFragment()
            } else {
                DetalleCitaMedicoFragment()
            }
            val bundle = Bundle().apply {
                putString("citaId", citaSeleccionada.idCita)
                putString("estado", citaSeleccionada.estado)
            }
            fragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragmento, fragment)
                .addToBackStack(null)
                .commit()
        }
        rvCitas.adapter = adapter
        cargarHistorial()
    }

    private fun cargarHistorial() {
        sesion.actualizarListaCitas {
            val listaOrdenada = sesion.listaOrdenada().anteriores()
            adapter?.actualizarDatos(listaOrdenada)
        }
    }

    override fun onResume() {
        super.onResume()
        val tvEncabezado: TextView? =
            (activity as? frmPrincipalActivity)?.findViewById(R.id.encabezadoPrincipal)
        tvEncabezado?.text = "Historial"
    }

    fun ArrayList<cita>.anteriores(): ArrayList<cita> {
        val lista = ArrayList<cita>()
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val hoy = formato.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
        for (cita in this) {
            if (!cita.hora.isNullOrEmpty()) {
                try {
                    val fechaCita = formato.parse("${cita.fecha} ${cita.hora?.trim()}")
                    if (fechaCita.before(hoy)) {
                        if (cita.estado.equals("Pendiente")){
                            cita.estado = "Cancelada por Ausencia"
                        }
                        lista.add(cita)
                    }
                } catch (e: Exception) {
                    Log.e("ParseError", "Error al parsear la cita: '${cita.fecha} ${cita.hora}'", e)
                }
            } else {
                Log.e("NullError", "La hora de la cita es nula o vacía: '${cita.fecha}'")
            }
        }
        return lista
    }

}
