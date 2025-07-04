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
    filtro: Boolean,
    val onCitaSelected: (cita) -> Unit
) : ArrayAdapter<cita>(context, 0, lista) {

    val tipo: String = tipo

    //A debate si se use un metodo vistaNormal
    override fun getView(position: Int, converterView: View?, parent: ViewGroup): View {
        val c = lista[position]
        if (c.idCita == "encabezado") {
            return vistaEncabezado(position, converterView, parent)
        } else {
            return vistaNormal(position, converterView, parent)
        }

    }

    fun vistaEncabezado(position: Int, convertView: View?, parent: ViewGroup): View {
        val c = lista[position]
        val vista: View
        vista = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.encabezado_citas, parent, false)
        vista.findViewById<TextView>(R.id.textoEncabezado).text = c.fecha
        return vista
    }

    fun vistaNormal(position: Int, convertView: View?, parent: ViewGroup): View {
        val c = lista[position]
        val vista: View
        vista = convertView ?: LayoutInflater.from(context).inflate(
            if (tipo == "medico") R.layout.cita_medico else R.layout.cita_paciente, parent, false
        )
        val nombrePacienteTextView = vista.findViewById<TextView>(R.id.citaPaciente)
        val nombreMedicoTextView = vista.findViewById<TextView>(R.id.citaMedico)
        nombrePacienteTextView.text = "Cargando..."
        nombreMedicoTextView.text = "Cargando..."
        cargarDatosPacienteYMedico(c, nombrePacienteTextView, nombreMedicoTextView)
        vista.findViewById<TextView>(R.id.citaFecha).text = c.fecha
        vista.findViewById<TextView>(R.id.citaHora).text = c.hora
        vista.findViewById<TextView>(R.id.citaMotivo).text = c.motivo
        vista.findViewById<TextView>(R.id.citaEspecialidad).text = c.especialidad
        val panelCita = vista.findViewById<LinearLayout>(R.id.panelCitaPaciente)
        panelCita.setOnClickListener {
            onCitaSelected(c)
        }
        return vista
    }
    private fun cargarDatosPacienteYMedico(cita: cita, nombrePacienteTextView: TextView, nombreMedicoTextView: TextView) {
        sesion.buscarMedico(cita.idMedico ?: "") { medico ->
            if (medico != null) {
                nombreMedicoTextView.text = medico.nombre
            } else {
                nombreMedicoTextView.text = "Desconocido"
            }
        }

        sesion.buscarPaciente(cita.idPaciente ?: "") { paciente ->
            if (paciente != null) {
                nombrePacienteTextView.text = paciente.nombre
            } else {
                nombrePacienteTextView.text = "Desconocido"
            }
        }
    }
}

