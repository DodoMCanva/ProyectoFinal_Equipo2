package equipo.dos.citasmedicas.Fragmentos

import Persistencia.sesion
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Switch
import android.widget.TextView
import equipo.dos.citasmedicas.R
import modulos.AdapterCita
import modulos.AdapterHistorial
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class HistorialFragment : Fragment() {
    var adapter: AdapterHistorial? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_historial, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AdapterHistorial(requireContext(), sesion.citas, sesion.tipo)
        var listaCitas = view.findViewById<ListView>(R.id.lvHistorial)
        listaCitas.adapter = adapter

    }
}