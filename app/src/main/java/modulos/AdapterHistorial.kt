package modulos

import Persistencia.cita
import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import equipo.dos.citasmedicas.R

class AdapterHistorial(
    context: Context,
    lista: ArrayList<cita>,
    val tipo: String,
    val onCitaSelected: (cita) -> Unit
) : ArrayAdapter<cita>(context, 0, lista) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val c = getItem(position)!!

        val vista: View
        if (tipo == "medico") {
            vista = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.cita_medico, parent, false)
            vista.findViewById<TextView>(R.id.citaMFecha).text = c.fecha
            vista.findViewById<TextView>(R.id.citaMHora).text = c.hora
            vista.findViewById<TextView>(R.id.citaPaciente).text = c.nombrePaciente
            vista.findViewById<TextView>(R.id.citaMotivo).text = c.motivo
            vista.findViewById<TextView>(R.id.citaEstado).text = c.estado

            val selCita = vista.findViewById<LinearLayout>(R.id.panelCitaMedico)
            when (c.estado) {
                "Completada" -> selCita.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.BackCitaCompletada
                    )
                )

                "Pendiente" -> selCita.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.BackCitaPendiente
                    )
                )

                "Cancelada" -> selCita.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.BackCitaCancelada
                    )
                )

                else -> selCita.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.BackCitaPendiente
                    )
                )
            }

            selCita.setOnClickListener {
                onCitaSelected(c)
            }

        } else {
            vista = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.cita_paciente, parent, false)
            vista.findViewById<TextView>(R.id.citaFecha).text = c.fecha
            vista.findViewById<TextView>(R.id.citaHora).text = c.hora
            vista.findViewById<TextView>(R.id.citaEspecialidad).text = c.especialidad
            vista.findViewById<TextView>(R.id.citaMedico).text = c.nombreMedico
            val selecCita = vista.findViewById<LinearLayout>(R.id.panelCitaPaciente)
            selecCita.setOnClickListener {
                onCitaSelected(c)
            }
        }
        return vista
    }

    fun actualizarDatos(nuevasCitas: List<cita>) {
        clear()
        addAll(nuevasCitas)
        notifyDataSetChanged()
    }
}

