package Persistencia

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import equipo.dos.citasmedicas.R
import equipo.dos.citasmedicas.frmAgendarMedicoActivity
import equipo.dos.citasmedicas.frmDetalleCitaActivity
import equipo.dos.citasmedicas.frmDetalleCitaMedicoPendienteActivity

class AdapterCita(context: Context, val lista: ArrayList<cita>, tipo : String): ArrayAdapter<cita>(context,0, lista) {
    val tipo : String = tipo
    override fun getView(position: Int, converterView: View?, parent: ViewGroup): View {
        val c = lista[position]
        val vista: View

        if (tipo == "medico") {
            vista = converterView?: LayoutInflater.from(context).inflate(R.layout.cita_medico, parent, false)
            vista.findViewById<TextView>(R.id.citaMFecha).text = c.fecha
            vista.findViewById<TextView>(R.id.citaMHora).text = c.hora
            vista.findViewById<TextView>(R.id.citaPaciente).text = c.paciente.nombre
            vista.findViewById<TextView>(R.id.citaMotivo).text = c.motivo
            vista.findViewById<TextView>(R.id.citaEstado).text = c.estado

            val selCita = vista.findViewById<LinearLayout>(R.id.panelCitaMedico)
            selCita.setOnClickListener {
                val intent = Intent(context, frmDetalleCitaMedicoPendienteActivity::class.java)
                context.startActivity(intent)
            }

        } else {
            vista = converterView?: LayoutInflater.from(context).inflate(R.layout.cita_paciente, parent, false)
            vista.findViewById<TextView>(R.id.citaFecha).text = c.fecha
            vista.findViewById<TextView>(R.id.citaHora).text = c.hora
            vista.findViewById<TextView>(R.id.citaEspecialidad).text = c.medico.especialidad
            vista.findViewById<TextView>(R.id.citaMedico).text = c.medico.nombre

            val selecMedico = vista.findViewById<LinearLayout>(R.id.panelCitaPaciente)
            selecMedico.setOnClickListener {
                val intent = Intent(context, frmDetalleCitaActivity::class.java)
                context.startActivity(intent)
            }
        }

        return vista
    }

}
