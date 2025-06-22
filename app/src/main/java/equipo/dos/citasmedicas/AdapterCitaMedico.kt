package equipo.dos.citasmedicas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView

class AdapterCitaMedico(context: Context, private val citas: List<CitaMedico>) :
    ArrayAdapter<CitaMedico>(context, 0, citas) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val vista = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.cita_medico, parent, false)

        val cita = citas[position]

        // Seteamos los datos normales
        vista.findViewById<TextView>(R.id.citaMFecha).text = cita.fecha
        vista.findViewById<TextView>(R.id.citaMHora).text = cita.hora
        vista.findViewById<TextView>(R.id.citaPaciente).text = cita.paciente
        vista.findViewById<TextView>(R.id.citaMotivo).text = cita.motivo
        vista.findViewById<TextView>(R.id.citaEstado).text = cita.estado

        // Cambiar el fondo según el estado
        val layoutPanel = vista.findViewById<LinearLayout>(R.id.panelCita)
        when (cita.estado.lowercase()) {
            "completada" -> layoutPanel.setBackgroundResource(R.drawable.fondocitacompletada)
            "cancelada" -> layoutPanel.setBackgroundResource(R.drawable.fondocitacancelada)
            "pendiente" -> layoutPanel.setBackgroundResource(R.drawable.fondocitapendiente)
            else -> layoutPanel.setBackgroundResource(R.drawable.fondocitapendiente) // default
        }

        return vista
    }
}