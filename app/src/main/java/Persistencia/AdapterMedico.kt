package Persistencia

import android.animation.TypeConverter
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.IntentSenderRequest
import equipo.dos.citasmedicas.R
import equipo.dos.citasmedicas.frmAgendarMedicoActivity

class AdapterMedico(context: Context, val lista: ArrayList<medico>): ArrayAdapter<medico>(context,0, lista) {

    override fun getView(position: Int, converterView: View?, parent: ViewGroup ): View{
        val vista= converterView?: LayoutInflater.from(context).inflate(R.layout.campo_medico, parent, false)
        val me= lista[position]
        vista.findViewById<TextView>(R.id.medicoEspecialidad).text= me.especialidad
        vista.findViewById<TextView>(R.id.medicoNombre).text= me.nombre
        val layoutPanel= vista.findViewById<LinearLayout>(R.id.btnCampoMedico)
        var selecMedico: LinearLayout= vista.findViewById(R.id.btnCampoMedico)
        selecMedico.setOnClickListener {
            var intent: Intent= Intent(context, frmAgendarMedicoActivity::class.java)
            context!!.startActivity(intent)
        }
       return vista
    }

}