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

class AdapterCita(
    context: Context,
    val lista: ArrayList<cita>,
    tipo: String,
    filtro: Boolean,
    fecha : String,
    val onCitaSelected : (cita) -> Unit
) : ArrayAdapter<cita>(context, 0, lista) {

    val tipo: String = tipo
    val filtro: Boolean = filtro
    val fecha = fecha

    override fun getView(position: Int, converterView: View?, parent: ViewGroup): View {
        //
        return vistaNormal(position, converterView, parent)
    }

    //
    fun ordenarPorFechaHora(){

    }

    //
    fun filtrar(){

    }

    fun filtrarDia(){}



    fun vistaNormal(position: Int, converterView: View?, parent: ViewGroup): View {

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
                    selCita.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.BackCitaPendiente
                        )
                    )
                }
            }

            //Cambio comportamiento
            selCita.setOnClickListener {
                val intent = Intent(context, frmDetalleCitaMedicoPendienteActivity::class.java)
                intent.putExtra("citaId", c.idCita)
                context.startActivity(intent)
            }
        } else {
            vista = converterView ?: LayoutInflater.from(context)
                .inflate(R.layout.cita_paciente, parent, false)
            vista.findViewById<TextView>(R.id.citaFecha).text = c.fecha
            vista.findViewById<TextView>(R.id.citaHora).text = c.hora
            vista.findViewById<TextView>(R.id.citaMedico).text = c.nombreMedico

            val selecCita = vista.findViewById<LinearLayout>(R.id.panelCitaPaciente)
            //Cambio comportamiento
            selecCita.setOnClickListener {
                onCitaSelected(c)
            }
        }
        return vista
    }

    //Esta vista muestra todas las citas de la semana a partir de la fecha establecida(si la fecha es el 1 de enero, imprimira las citas,
    // de los dias 1, 2, 3, 4, 5, 6, 7)
    fun vistaFiltrada() {
    }
}
