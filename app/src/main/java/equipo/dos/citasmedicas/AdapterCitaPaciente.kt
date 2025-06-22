package equipo.dos.citasmedicas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class AdapterCitaPaciente(context: Context, private val citas: List<CitaPaciente>) :
    ArrayAdapter<CitaPaciente>(context, 0, citas) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val vista = convertView ?: LayoutInflater.from(context).inflate(R.layout.cita_paciente, parent, false)

        val cita = citas[position]

        vista.findViewById<TextView>(R.id.citaFecha).text = cita.fecha
        vista.findViewById<TextView>(R.id.citaHora).text = cita.hora
        vista.findViewById<TextView>(R.id.citaEspecialidad).text = cita.especialidad
        vista.findViewById<TextView>(R.id.citaMedico).text = cita.medico

        return vista
    }
}