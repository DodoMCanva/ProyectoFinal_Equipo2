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
        val listaCitas: ListView = view.findViewById(R.id.lvCitas)
        val btnAgendar: FloatingActionButton? = view.findViewById(R.id.btnAgendar)
        adapter = null
        adaptarCitas()
        listaCitas.adapter = adapter

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
            fechaBusqueda = fechaTexto.text.toString()
            adapter = null
            adaptarCitas()
            listaCitas.adapter = adapter
        }

        filtro.setOnClickListener {
            filtroBusqueda = !filtroBusqueda
            adapter = null
            adaptarCitas()
            listaCitas.adapter = adapter
        }

    }

    fun adaptarCitas() {
        Log.d("CITAS", ">>> imprimirCitas llamada")
        sesion.actualizarListaCitas {
            Log.d("CITAS", ">>> Callback ejecutado - citas.size = ${sesion.citas.size}")
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

            }
        }
    }
}