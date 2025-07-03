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

class AdapterHistorial(context: Context, val lista: ArrayList<cita>, tipo : String): ArrayAdapter<cita>(context,0, lista) {
    val tipo: String = tipo

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, converterView: View?, parent: ViewGroup): View {
        val c = lista[position]
        var m : medico? = null
        val uidMedico = c.idMedico
        if (uidMedico != null) {
            sesion.buscarMedico(uidMedico) { medico ->
                if (medico != null) {
                    m = medico
                } else {
                    Log.e("Firebase", "No se encontró al médico")
                }
            }
        } else {
            Log.e("Firebase", "El UID del médico es nulo")
        }

        var p : paciente? = null
        val uidPaciente = c.idPaciente
        if (uidPaciente != null) {
            sesion.buscarPaciente(uidPaciente) { paciente ->
                if (paciente != null) {
                    p = paciente
                } else {
                    Log.e("Firebase", "No se encontró al paciente")
                }
            }
        } else {
            Log.e("Firebase", "El UID del paciente es nulo")
        }

        val vista: View
        if (tipo == "medico") {
            vista = converterView ?: LayoutInflater.from(context)
                .inflate(R.layout.cita_medico, parent, false)
            vista.findViewById<TextView>(R.id.citaMFecha).text = c.fecha
            vista.findViewById<TextView>(R.id.citaMHora).text = c.hora
            vista.findViewById<TextView>(R.id.citaPaciente).text = c.nombrePaciente
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

            }
        } else {
            vista = converterView ?: LayoutInflater.from(context)
                .inflate(R.layout.cita_paciente, parent, false)
            vista.findViewById<TextView>(R.id.citaFecha).text = c.fecha
            vista.findViewById<TextView>(R.id.citaHora).text = c.hora
            vista.findViewById<TextView>(R.id.citaEspecialidad).text = m?.especialidad
            vista.findViewById<TextView>(R.id.citaMedico).text = m?.nombre

            val selecMedico = vista.findViewById<LinearLayout>(R.id.panelCitaPaciente)
            selecMedico.setOnClickListener {

            }
        }
        return vista
    }
}
