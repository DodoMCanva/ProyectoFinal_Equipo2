package modulos

import Persistencia.cita
import Persistencia.sesion
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import equipo.dos.citasmedicas.R
import androidx.recyclerview.widget.RecyclerView

class AdapterCitaRecycler(
    private val context: Context,
    private val lista: List<cita>,
    private val tipo: String,
    private val onCitaSelected: (cita) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ENCABEZADO = 0
        private const val TYPE_CITA = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (lista[position].idCita == "encabezado") TYPE_ENCABEZADO else TYPE_CITA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ENCABEZADO) {
            val view = LayoutInflater.from(context).inflate(R.layout.encabezado_citas, parent, false)
            EncabezadoViewHolder(view)
        } else {
            val layout = if (tipo == "medico") R.layout.cita_medico else R.layout.cita_paciente
            val view = LayoutInflater.from(context).inflate(layout, parent, false)
            CitaViewHolder(view)
        }
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cita = lista[position]
        if (holder is EncabezadoViewHolder) {
            holder.bind(cita)
        } else if (holder is CitaViewHolder) {
            holder.bind(cita)
        }
    }

    inner class EncabezadoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textoEncabezado: TextView = itemView.findViewById(R.id.textoEncabezado)
        fun bind(cita: cita) {
            textoEncabezado.text = cita.fecha
        }
    }

    inner class CitaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(cita: cita) {
            if (tipo == "medico") {
                itemView.findViewById<TextView>(R.id.citaMFecha).text = cita.fecha
                itemView.findViewById<TextView>(R.id.citaMHora).text = cita.hora
                itemView.findViewById<TextView>(R.id.citaMotivo).text = cita.motivo
                itemView.findViewById<TextView>(R.id.citaEstado).text = cita.estado

                cita.idPaciente?.let { idPaciente ->
                    sesion.buscarPaciente(idPaciente) { paciente ->
                        paciente?.let {
                            itemView.findViewById<TextView>(R.id.citaPaciente).text = it.nombre
                        }
                    }
                }

                val selCita = itemView.findViewById<LinearLayout>(R.id.panelCitaMedico)
                val colorResId = when (cita.estado) {
                    "Completada" -> R.drawable.fondocitacompletada
                    "Pendiente" -> R.drawable.fondocitapendiente
                    "Cancelada" -> R.drawable.fondocitacancelada
                    else -> R.drawable.fondocitapendiente
                }
                selCita.setBackgroundResource(colorResId)

                selCita.setOnClickListener {
                    onCitaSelected(cita)
                }
            } else {
                itemView.findViewById<TextView>(R.id.citaFecha)?.text = cita.fecha
                itemView.findViewById<TextView>(R.id.citaHora)?.text = cita.hora

                cita.idMedico?.let { idMedico ->
                    sesion.buscarMedico(idMedico) { medico ->
                        medico?.let {
                            itemView.findViewById<TextView>(R.id.citaMedico)?.text = it.nombre
                            itemView.findViewById<TextView>(R.id.citaEspecialidad)?.text = it.especialidad
                        }
                    }
                }

                itemView.findViewById<LinearLayout>(R.id.panelCitaPaciente)?.setOnClickListener {
                    onCitaSelected(cita)
                }
            }
        }
    }
}
