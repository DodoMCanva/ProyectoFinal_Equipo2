package equipo.dos.citasmedicas.Fragmentos

import Persistencia.sesion
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import equipo.dos.citasmedicas.R
import equipo.dos.citasmedicas.frmPrincipalActivity
import modulos.AdapterHistorial

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
        val listaCitasView = view.findViewById<ListView>(R.id.lvHistorial)
        adapter = AdapterHistorial(requireContext(), ArrayList(), sesion.tipo) { citaSeleccionada ->
            val fragment = if (sesion.tipo == "paciente") {
                DetalleCitaPacienteFragment()
            } else {
                DetalleCitaMedicoFragment()
            }
            val bundle = Bundle().apply {
                putString("citaId", citaSeleccionada.idCita)
            }
            fragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragmento, fragment)
                .addToBackStack(null).commit()

        }
        listaCitasView.adapter = adapter
        cargarHistorial()
    }

    private fun cargarHistorial() {
        sesion.actualizarListaCitas {
            val listaOrdenada = sesion.listaOrdenada()
            adapter?.actualizarDatos(listaOrdenada)
        }
    }

    override fun onResume() {
        super.onResume()
        val tvEncabezado: TextView? = (activity as? frmPrincipalActivity)?.findViewById(R.id.encabezadoPrincipal)
        tvEncabezado?.text = "Historial"
    }
}
