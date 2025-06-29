package modulos
import Persistencia.cita
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import equipo.dos.citasmedicas.R
import equipo.dos.citasmedicas.frmDetalleCitaActivity
import equipo.dos.citasmedicas.frmDetalleCitaMedicoPendienteActivity

class AdapterHistorial(context: Context, val lista: ArrayList<cita>, tipo : String): ArrayAdapter<cita>(context,0, lista) {
    val tipo: String = tipo

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, converterView: View?, parent: ViewGroup): View {
        val c = lista[position]
        val vista: View
        if (tipo == "medico") {
            vista = converterView ?: LayoutInflater.from(context)
                .inflate(R.layout.cita_medico, parent, false)
            vista.findViewById<TextView>(R.id.citaMFecha).text = c.fecha
            vista.findViewById<TextView>(R.id.citaMHora).text = c.hora
            vista.findViewById<TextView>(R.id.citaPaciente).text = c.paciente.nombre
            vista.findViewById<TextView>(R.id.citaMotivo).text = c.motivo
            vista.findViewById<TextView>(R.id.citaEstado).text = c.estado

            val selCita = vista.findViewById<LinearLayout>(R.id.panelCitaMedico)
            when (c.estado) {
                "Completada" -> {
                    selCita.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.BackCitaCompletada
                        )
                    )
                }

                "Pendiente" -> {
                    selCita.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.BackCitaPendiente
                        )
                    )
                }

                "Cancelada" -> {
                    selCita.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.BackCitaCancelada
                        )
                    )
                }

                else -> {
                }
            }
            selCita.setOnClickListener {
                val intent = Intent(context, frmDetalleCitaMedicoPendienteActivity::class.java)
                intent.putExtra("nombre", c.paciente.nombre)
                intent.putExtra("genero", c.paciente.genero)
                intent.putExtra("telefono", c.paciente.telefono)
                intent.putExtra("fecha", c.fecha)
                intent.putExtra("hora", c.hora)
                intent.putExtra("estado", c.estado)
                intent.putExtra("motivo", c.motivo)
                intent.putExtra("edad", c.paciente.calcularEdad())
                context!!.startActivity(intent)
            }
        } else {
            vista = converterView ?: LayoutInflater.from(context)
                .inflate(R.layout.cita_paciente, parent, false)
            vista.findViewById<TextView>(R.id.citaFecha).text = c.fecha
            vista.findViewById<TextView>(R.id.citaHora).text = c.hora
            vista.findViewById<TextView>(R.id.citaEspecialidad).text = c.medico.especialidad
            vista.findViewById<TextView>(R.id.citaMedico).text = c.medico.nombre

            val selecMedico = vista.findViewById<LinearLayout>(R.id.panelCitaPaciente)
            selecMedico.setOnClickListener {
                val intent = Intent(context, frmDetalleCitaActivity::class.java)
                intent.putExtra("nombre", c.medico.nombre)
                intent.putExtra("especialidad", c.medico.especialidad)
                intent.putExtra("fecha", c.fecha)
                intent.putExtra("hora", c.hora)
                intent.putExtra("estado", c.estado)
                intent.putExtra("motivo", c.motivo)
                context!!.startActivity(intent)
            }
        }
        return vista
    }
}
