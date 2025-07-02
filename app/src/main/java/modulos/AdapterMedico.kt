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

class AdapterMedico(
    context: Context,
    val lista: ArrayList<medico>,
    val onMedicoSelected: (medico) -> Unit
) : ArrayAdapter<medico>(context, 0, lista) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val vista = convertView ?: LayoutInflater.from(context).inflate(R.layout.campo_medico, parent, false)
        val me = lista[position]
        vista.findViewById<TextView>(R.id.medicoEspecialidad).text = me.especialidad
        vista.findViewById<TextView>(R.id.medicoNombre).text = me.nombre

        val selecMedico: LinearLayout = vista.findViewById(R.id.btnCampoMedico)
        selecMedico.setOnClickListener {
            onMedicoSelected(me)
        }

        return vista
    }

}