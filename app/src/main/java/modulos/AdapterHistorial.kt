package modulos

import Persistencia.cita
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import equipo.dos.citasmedicas.R

class AdapterHistorial(
    private val context: Context,
    private var listaCitas: List<cita>,
    private val tipo: String,
    private val onCitaSelected: (cita) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // ViewType para médico o paciente
    private val TYPE_MEDICO = 1
    private val TYPE_PACIENTE = 2

    override fun getItemViewType(position: Int): Int {
        return if (tipo == "medico") TYPE_MEDICO else TYPE_PACIENTE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return if (viewType == TYPE_MEDICO) {
            val view = inflater.inflate(R.layout.cita_medico, parent, false)
            MedicoViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.cita_paciente, parent, false)
            PacienteViewHolder(view)
        }
    }

    override fun getItemCount(): Int = listaCitas.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val c = listaCitas[position]

        if (holder is MedicoViewHolder) {
            holder.fecha.text = c.fecha
            holder.hora.text = c.hora
            holder.nombrePaciente.text = c.nombrePaciente
            holder.motivo.text = c.motivo
            holder.estado.text = c.estado

            val drawableRes = when (c.estado) {
                "Completada" -> R.drawable.fondocitacompletada
                "Pendiente" -> R.drawable.fondocitapendiente
                "Cancelada" -> R.drawable.fondocitacancelada
                else -> R.drawable.fondocitapendiente
            }
            holder.panel.background = ContextCompat.getDrawable(context, drawableRes)
            holder.panel.setOnClickListener { onCitaSelected(c) }

        } else if (holder is PacienteViewHolder) {
            holder.fecha.text = c.fecha
            holder.hora.text = c.hora
            holder.especialidad.text = c.especialidad
            holder.nombreMedico.text = c.nombreMedico
            holder.panel.setOnClickListener { onCitaSelected(c) }
        }
    }

    fun actualizarDatos(nuevasCitas: List<cita>) {
        listaCitas = nuevasCitas
        notifyDataSetChanged()
    }

    inner class MedicoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fecha: TextView = view.findViewById(R.id.citaMFecha)
        val hora: TextView = view.findViewById(R.id.citaMHora)
        val nombrePaciente: TextView = view.findViewById(R.id.citaPaciente)
        val motivo: TextView = view.findViewById(R.id.citaMotivo)
        val estado: TextView = view.findViewById(R.id.citaEstado)
        val panel: LinearLayout = view.findViewById(R.id.panelCitaMedico)
    }

    inner class PacienteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fecha: TextView = view.findViewById(R.id.citaFecha)
        val hora: TextView = view.findViewById(R.id.citaHora)
        val especialidad: TextView = view.findViewById(R.id.citaEspecialidad)
        val nombreMedico: TextView = view.findViewById(R.id.citaMedico)
        val panel: LinearLayout = view.findViewById(R.id.panelCitaPaciente)
    }
}
