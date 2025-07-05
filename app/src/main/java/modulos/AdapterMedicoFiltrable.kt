package modulos

import Persistencia.medico
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import equipo.dos.citasmedicas.R

class AdapterMedicoFiltrable(
    context: Context,
    private var medicos: List<medico>,
    val onMedicoSelected: (medico) -> Unit
) : ArrayAdapter<medico>(context, 0, medicos) {

    override fun getCount(): Int = medicos.size
    override fun getItem(position: Int): medico? = medicos[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val vista = convertView ?: LayoutInflater.from(context).inflate(R.layout.campo_medico, parent, false)
        val me = medicos[position]

        vista.findViewById<TextView>(R.id.medicoEspecialidad).text = me.especialidad
        vista.findViewById<TextView>(R.id.medicoNombre).text = me.nombre

        vista.findViewById<LinearLayout>(R.id.btnCampoMedico).setOnClickListener {
            onMedicoSelected(me)
        }

        return vista
    }

    fun actualizarLista(nuevaLista: List<medico>) {
        this.medicos = nuevaLista
        notifyDataSetChanged()
    }
}
