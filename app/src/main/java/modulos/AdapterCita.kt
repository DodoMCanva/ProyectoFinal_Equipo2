package modulos

import Persistencia.cita
import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import equipo.dos.citasmedicas.R

class AdapterCita(
    context: Context,
    val lista: ArrayList<cita>,
    tipo: String,
    val onCitaSelected: (cita) -> Unit
) : ArrayAdapter<cita>(context, 0, lista) {

    val tipo: String = tipo

    override fun getView(position: Int, converterView: View?, parent: ViewGroup): View {
        val c = lista[position]
        if (c.idCita == "encabezado") {
            return vistaEncabezado(position, converterView, parent)
        } else {
            return vistaNormal(position, converterView, parent)
        }

    }

    fun vistaEncabezado(position: Int, convertView: View?, parent: ViewGroup): View {
        val c = getItem(position)!!
        val vista: View
        vista = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.encabezado_citas, parent, false)
        vista.findViewById<TextView>(R.id.textoEncabezado).text = c.fecha
        return vista
    }

    fun vistaNormal(position: Int, converterView: View?, parent: ViewGroup): View {
        val c = lista[position]
        val vista: View

        if (tipo == "medico") {
            vista = converterView ?: LayoutInflater.from(context)
                .inflate(R.layout.cita_medico, parent, false)

            vista.findViewById<TextView>(R.id.citaMFecha).text = c.fecha
            vista.findViewById<TextView>(R.id.citaMHora).text = c.hora
            vista.findViewById<TextView>(R.id.citaMotivo).text = c.motivo
            vista.findViewById<TextView>(R.id.citaEstado).text = c.estado

            c.idPaciente?.let { idPaciente ->
                sesion.buscarPaciente(idPaciente) { paciente ->
                    paciente?.let {
                        vista.findViewById<TextView>(R.id.citaPaciente).text = it.nombre
                    }
                }
            }

            val selCita = vista.findViewById<LinearLayout>(R.id.panelCitaMedico)
            val colorResId = when (c.estado) {
                "Completada" -> R.color.BackCitaCompletada
                "Pendiente" -> R.color.BackCitaPendiente
                "Cancelada" -> R.color.BackCitaCancelada
                else -> R.color.BackCitaPendiente
            }
            selCita.setBackgroundColor(ContextCompat.getColor(context, colorResId))

            selCita.setOnClickListener {
                onCitaSelected(c)
            }
        } else {
            vista = converterView ?: LayoutInflater.from(context)
                .inflate(R.layout.cita_paciente, parent, false)

            vista.findViewById<TextView>(R.id.citaFecha)?.text = c.fecha
            vista.findViewById<TextView>(R.id.citaHora)?.text = c.hora

            c.idMedico?.let { idMedico ->
                sesion.buscarMedico(idMedico) { medico ->
                    medico?.let {
                        vista.findViewById<TextView>(R.id.citaMedico)?.text = it.nombre
                        vista.findViewById<TextView>(R.id.citaEspecialidad)?.text = it.especialidad
                    }
                }
            }

            vista.findViewById<LinearLayout>(R.id.panelCitaPaciente)?.setOnClickListener {
                onCitaSelected(c)
            }
        }
        return vista
    }
}

