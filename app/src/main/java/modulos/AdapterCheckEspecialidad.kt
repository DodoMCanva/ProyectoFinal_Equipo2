package modulos

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.content.Context
import android.view.LayoutInflater
import android.widget.CheckBox
import equipo.dos.citasmedicas.R


class AdapterCheckEspecialidad(
    private val context: Context,
    private val especialidades: List<String>
) : BaseAdapter() {

    private val seleccionados = BooleanArray(especialidades.size)

    var onSeleccionCambio: (() -> Unit)? = null

    override fun getCount(): Int = especialidades.size

    override fun getItem(position: Int): String = especialidades[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.especialidad, parent, false)
        val checkBox = view.findViewById<CheckBox>(R.id.cbCategoria)

        checkBox.text = especialidades[position]
        checkBox.setOnCheckedChangeListener(null)
        checkBox.isChecked = seleccionados[position]

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            seleccionados[position] = isChecked
            onSeleccionCambio?.invoke()
        }

        return view
    }

    fun getSeleccionados(): List<String> {
        return especialidades.filterIndexed { index, _ -> seleccionados[index] }
    }
}
