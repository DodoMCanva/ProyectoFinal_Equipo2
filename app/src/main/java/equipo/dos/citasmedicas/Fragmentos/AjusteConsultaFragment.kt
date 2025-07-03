package equipo.dos.citasmedicas.Fragmentos

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import equipo.dos.citasmedicas.R
import equipo.dos.citasmedicas.helpers.MenuDesplegable
class AjusteConsultaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ajuste_consulta, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Cambio de horario de consulta

        val ids = listOf(
            R.id.etLunesDeManana, R.id.etLunesAManana,
            R.id.etLunesDeTarde, R.id.etLunesATarde,
            R.id.etMartesDeManana, R.id.etMartesAManana,
            R.id.etMartesDeTarde, R.id.etMartesATarde,
            R.id.etMiercolesDeManana, R.id.etMiercolesAManana,
            R.id.etMiercolesDeTarde, R.id.etMiercolesATarde,
            R.id.etJuevesDeManana, R.id.etJuevesAManana,
            R.id.etJuevesDeTarde, R.id.etJuevesATarde,
            R.id.etViernesDeManana, R.id.etViernesAManana,
            R.id.etViernesDeTarde, R.id.etViernesATarde,
            R.id.etSabadoDeManana, R.id.etSabadoAManana,
            R.id.etSabadoDeTarde, R.id.etSabadoATarde,
            R.id.etDomingoDeManana, R.id.etDomingoAManana,
            R.id.etDomingoDeTarde, R.id.etDomingoATarde
        )

        for (id in ids) {
            val editText = view.findViewById<EditText>(id)
            editText.setOnClickListener {
                showTimePicker(editText)
            }
        }
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarConsulta)
        btnGuardar.setOnClickListener {
            Toast.makeText(requireContext(), "Cambios guardados", Toast.LENGTH_SHORT).show()
        }

        // Lunes
        var lunesActivo = true
        val layoutLunes = view.findViewById<LinearLayout>(R.id.layoutLunes)
        val etLunesDeManana = view.findViewById<EditText>(R.id.etLunesDeManana)
        val etLunesAManana = view.findViewById<EditText>(R.id.etLunesAManana)
        val etLunesDeTarde = view.findViewById<EditText>(R.id.etLunesDeTarde)
        val etLunesATarde = view.findViewById<EditText>(R.id.etLunesATarde)

        layoutLunes.setOnClickListener {
            lunesActivo = !lunesActivo
            val alpha = if (lunesActivo) 1f else 0.4f
            val habilitado = lunesActivo
            etLunesDeManana.isEnabled = habilitado
            etLunesAManana.isEnabled = habilitado
            etLunesDeTarde.isEnabled = habilitado
            etLunesATarde.isEnabled = habilitado
            etLunesDeManana.alpha = alpha
            etLunesAManana.alpha = alpha
            etLunesDeTarde.alpha = alpha
            etLunesATarde.alpha = alpha
        }

        // Martes
        var martesActivo = true
        val layoutMartes = view.findViewById<LinearLayout>(R.id.layoutMartes)
        val etMartesDeManana = view.findViewById<EditText>(R.id.etMartesDeManana)
        val etMartesAManana = view.findViewById<EditText>(R.id.etMartesAManana)
        val etMartesDeTarde = view.findViewById<EditText>(R.id.etMartesDeTarde)
        val etMartesATarde = view.findViewById<EditText>(R.id.etMartesATarde)

        layoutMartes.setOnClickListener {
            martesActivo = !martesActivo
            val alpha = if (martesActivo) 1f else 0.4f
            val habilitado = martesActivo
            etMartesDeManana.isEnabled = habilitado
            etMartesAManana.isEnabled = habilitado
            etMartesDeTarde.isEnabled = habilitado
            etMartesATarde.isEnabled = habilitado
            etMartesDeManana.alpha = alpha
            etMartesAManana.alpha = alpha
            etMartesDeTarde.alpha = alpha
            etMartesATarde.alpha = alpha
        }

        // Miercoles
        var miercolesActivo = true
        val layoutMiercoles = view.findViewById<LinearLayout>(R.id.layoutMiercoles)
        val etMiercolesDeManana = view.findViewById<EditText>(R.id.etMiercolesDeManana)
        val etMiercolesAManana = view.findViewById<EditText>(R.id.etMiercolesAManana)
        val etMiercolesDeTarde = view.findViewById<EditText>(R.id.etMiercolesDeTarde)
        val etMiercolesATarde = view.findViewById<EditText>(R.id.etMiercolesATarde)

        layoutMiercoles.setOnClickListener {
            miercolesActivo = !miercolesActivo
            val alpha = if (miercolesActivo) 1f else 0.4f
            val habilitado = miercolesActivo
            etMiercolesDeManana.isEnabled = habilitado
            etMiercolesAManana.isEnabled = habilitado
            etMiercolesDeTarde.isEnabled = habilitado
            etMiercolesATarde.isEnabled = habilitado
            etMiercolesDeManana.alpha = alpha
            etMiercolesAManana.alpha = alpha
            etMiercolesDeTarde.alpha = alpha
            etMiercolesATarde.alpha = alpha
        }

        // Jueves
        var juevesActivo = true
        val layoutJueves = view.findViewById<LinearLayout>(R.id.layoutJueves)
        val etJuevesDeManana = view.findViewById<EditText>(R.id.etJuevesDeManana)
        val etJuevesAManana = view.findViewById<EditText>(R.id.etJuevesAManana)
        val etJuevesDeTarde = view.findViewById<EditText>(R.id.etJuevesDeTarde)
        val etJuevesATarde = view.findViewById<EditText>(R.id.etJuevesATarde)

        layoutJueves.setOnClickListener {
            juevesActivo = !juevesActivo
            val alpha = if (juevesActivo) 1f else 0.4f
            val habilitado = juevesActivo
            etJuevesDeManana.isEnabled = habilitado
            etJuevesAManana.isEnabled = habilitado
            etJuevesDeTarde.isEnabled = habilitado
            etJuevesATarde.isEnabled = habilitado
            etJuevesDeManana.alpha = alpha
            etJuevesAManana.alpha = alpha
            etJuevesDeTarde.alpha = alpha
            etJuevesATarde.alpha = alpha
        }

        // Viernes
        var viernesActivo = true
        val layoutViernes = view.findViewById<LinearLayout>(R.id.layoutViernes)
        val etViernesDeManana = view.findViewById<EditText>(R.id.etViernesDeManana)
        val etViernesAManana = view.findViewById<EditText>(R.id.etViernesAManana)
        val etViernesDeTarde = view.findViewById<EditText>(R.id.etViernesDeTarde)
        val etViernesATarde = view.findViewById<EditText>(R.id.etViernesATarde)

        layoutViernes.setOnClickListener {
            viernesActivo = !viernesActivo
            val alpha = if (viernesActivo) 1f else 0.4f
            val habilitado = viernesActivo
            etViernesDeManana.isEnabled = habilitado
            etViernesAManana.isEnabled = habilitado
            etViernesDeTarde.isEnabled = habilitado
            etViernesATarde.isEnabled = habilitado
            etViernesDeManana.alpha = alpha
            etViernesAManana.alpha = alpha
            etViernesDeTarde.alpha = alpha
            etViernesATarde.alpha = alpha
        }

        // Sábado
        var sabadoActivo = true
        val layoutSabado = view.findViewById<LinearLayout>(R.id.layoutSabado)
        val etSabadoDeManana = view.findViewById<EditText>(R.id.etSabadoDeManana)
        val etSabadoAManana = view.findViewById<EditText>(R.id.etSabadoAManana)
        val etSabadoDeTarde = view.findViewById<EditText>(R.id.etSabadoDeTarde)
        val etSabadoATarde = view.findViewById<EditText>(R.id.etSabadoATarde)

        layoutSabado.setOnClickListener {
            sabadoActivo = !sabadoActivo
            val alpha = if (sabadoActivo) 1f else 0.4f
            val habilitado = sabadoActivo
            etSabadoDeManana.isEnabled = habilitado
            etSabadoAManana.isEnabled = habilitado
            etSabadoDeTarde.isEnabled = habilitado
            etSabadoATarde.isEnabled = habilitado
            etSabadoDeManana.alpha = alpha
            etSabadoAManana.alpha = alpha
            etSabadoDeTarde.alpha = alpha
            etSabadoATarde.alpha = alpha
        }

        // Domingo
        var domingoActivo = true
        val layoutDomingo = view.findViewById<LinearLayout>(R.id.layoutDomingo)
        val etDomingoDeManana = view.findViewById<EditText>(R.id.etDomingoDeManana)
        val etDomingoAManana = view.findViewById<EditText>(R.id.etDomingoAManana)
        val etDomingoDeTarde = view.findViewById<EditText>(R.id.etDomingoDeTarde)
        val etDomingoATarde = view.findViewById<EditText>(R.id.etDomingoATarde)

        layoutDomingo.setOnClickListener {
            domingoActivo = !domingoActivo
            val alpha = if (domingoActivo) 1f else 0.4f
            val habilitado = domingoActivo
            etDomingoDeManana.isEnabled = habilitado
            etDomingoAManana.isEnabled = habilitado
            etDomingoDeTarde.isEnabled = habilitado
            etDomingoATarde.isEnabled = habilitado
            etDomingoDeManana.alpha = alpha
            etDomingoAManana.alpha = alpha
            etDomingoDeTarde.alpha = alpha
            etDomingoATarde.alpha = alpha
        }

    }

    private fun showTimePicker(editText: EditText) {
        val cal = java.util.Calendar.getInstance()
        val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = cal.get(java.util.Calendar.MINUTE)

        val picker = android.app.TimePickerDialog(this.requireContext(), { _, h, m ->
            val hora = String.format("%02d:%02d", h, m)
            editText.setText(hora)
        }, hour, minute, true)

        picker.show()
    }

}

