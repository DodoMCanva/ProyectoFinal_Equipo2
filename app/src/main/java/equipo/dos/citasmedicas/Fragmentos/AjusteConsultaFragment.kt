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
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import equipo.dos.citasmedicas.ConfiguracionHorario
import equipo.dos.citasmedicas.R
import equipo.dos.citasmedicas.helpers.MenuDesplegable
class AjusteConsultaFragment : Fragment() {

    private var lunesActivo = true
    private var martesActivo = true
    private var miercolesActivo = true
    private var juevesActivo = true
    private var viernesActivo = true
    private var sabadoActivo = true
    private var domingoActivo = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ajuste_consulta, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val etCostoCita = view.findViewById<EditText>(R.id.etCostoCita)

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

        // Lunes
        val tvLunesEstado = view.findViewById<TextView>(R.id.tvLunesEstado)
        val etLunesDeManana = view.findViewById<EditText>(R.id.etLunesDeManana)
        val etLunesAManana = view.findViewById<EditText>(R.id.etLunesAManana)
        val etLunesDeTarde = view.findViewById<EditText>(R.id.etLunesDeTarde)
        val etLunesATarde = view.findViewById<EditText>(R.id.etLunesATarde)

        tvLunesEstado.setOnClickListener {
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

            if (lunesActivo) {
                tvLunesEstado.text = getString(R.string.textoSubrayadoDeshabilitar)
                tvLunesEstado.setTextColor(ContextCompat.getColor(requireContext(), R.color.rojoBtn))
            } else {
                tvLunesEstado.text = getString(R.string.textoSubrayadoHabilitar)
                tvLunesEstado.setTextColor(ContextCompat.getColor(requireContext(), R.color.amarilloBtn))
            }
        }



        // Martes
        val tvMartesEstado = view.findViewById<TextView>(R.id.tvMartesEstado)
        val etMartesDeManana = view.findViewById<EditText>(R.id.etMartesDeManana)
        val etMartesAManana = view.findViewById<EditText>(R.id.etMartesAManana)
        val etMartesDeTarde = view.findViewById<EditText>(R.id.etMartesDeTarde)
        val etMartesATarde = view.findViewById<EditText>(R.id.etMartesATarde)

        tvMartesEstado.setOnClickListener {
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

            if (martesActivo) {
                tvMartesEstado.text = getString(R.string.textoSubrayadoDeshabilitar)
                tvMartesEstado.setTextColor(ContextCompat.getColor(requireContext(), R.color.rojoBtn))
            } else {
                tvMartesEstado.text = getString(R.string.textoSubrayadoHabilitar)
                tvMartesEstado.setTextColor(ContextCompat.getColor(requireContext(), R.color.amarilloBtn))
            }
        }

        // Miercoles
        val tvMiercolesEstado = view.findViewById<TextView>(R.id.tvMiercolesEstado)
        val etMiercolesDeManana = view.findViewById<EditText>(R.id.etMiercolesDeManana)
        val etMiercolesAManana = view.findViewById<EditText>(R.id.etMiercolesAManana)
        val etMiercolesDeTarde = view.findViewById<EditText>(R.id.etMiercolesDeTarde)
        val etMiercolesATarde = view.findViewById<EditText>(R.id.etMiercolesATarde)

        tvMiercolesEstado.setOnClickListener {
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

            if (miercolesActivo) {
                tvMiercolesEstado.text = getString(R.string.textoSubrayadoDeshabilitar)
                tvMiercolesEstado.setTextColor(ContextCompat.getColor(requireContext(), R.color.rojoBtn))
            } else {
                tvMiercolesEstado.text = getString(R.string.textoSubrayadoHabilitar)
                tvMiercolesEstado.setTextColor(ContextCompat.getColor(requireContext(), R.color.amarilloBtn))
            }
        }

        // Jueves
        val tvJuevesEstado = view.findViewById<TextView>(R.id.tvJuevesEstado)
        val etJuevesDeManana = view.findViewById<EditText>(R.id.etJuevesDeManana)
        val etJuevesAManana = view.findViewById<EditText>(R.id.etJuevesAManana)
        val etJuevesDeTarde = view.findViewById<EditText>(R.id.etJuevesDeTarde)
        val etJuevesATarde = view.findViewById<EditText>(R.id.etJuevesATarde)

        tvJuevesEstado.setOnClickListener {
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

            if (juevesActivo) {
                tvJuevesEstado.text = getString(R.string.textoSubrayadoDeshabilitar)
                tvJuevesEstado.setTextColor(ContextCompat.getColor(requireContext(), R.color.rojoBtn))
            } else {
                tvJuevesEstado.text = getString(R.string.textoSubrayadoHabilitar)
                tvJuevesEstado.setTextColor(ContextCompat.getColor(requireContext(), R.color.amarilloBtn))
            }
        }

        // Viernes
        val tvViernesEstado = view.findViewById<TextView>(R.id.tvViernesEstado)
        val etViernesDeManana = view.findViewById<EditText>(R.id.etViernesDeManana)
        val etViernesAManana = view.findViewById<EditText>(R.id.etViernesAManana)
        val etViernesDeTarde = view.findViewById<EditText>(R.id.etViernesDeTarde)
        val etViernesATarde = view.findViewById<EditText>(R.id.etViernesATarde)

        tvViernesEstado.setOnClickListener {
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

            if (viernesActivo) {
                tvViernesEstado.text = getString(R.string.textoSubrayadoDeshabilitar)
                tvViernesEstado.setTextColor(ContextCompat.getColor(requireContext(), R.color.rojoBtn))
            } else {
                tvViernesEstado.text = getString(R.string.textoSubrayadoHabilitar)
                tvViernesEstado.setTextColor(ContextCompat.getColor(requireContext(), R.color.amarilloBtn))
            }
        }

        // Sábado
        val tvSabadoEstado = view.findViewById<TextView>(R.id.tvSabadoEstado)
        val etSabadoDeManana = view.findViewById<EditText>(R.id.etSabadoDeManana)
        val etSabadoAManana = view.findViewById<EditText>(R.id.etSabadoAManana)
        val etSabadoDeTarde = view.findViewById<EditText>(R.id.etSabadoDeTarde)
        val etSabadoATarde = view.findViewById<EditText>(R.id.etSabadoATarde)

        tvSabadoEstado.setOnClickListener {
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

            if (sabadoActivo) {
                tvSabadoEstado.text = getString(R.string.textoSubrayadoDeshabilitar)
                tvSabadoEstado.setTextColor(ContextCompat.getColor(requireContext(), R.color.rojoBtn))
            } else {
                tvSabadoEstado.text = getString(R.string.textoSubrayadoHabilitar)
                tvSabadoEstado.setTextColor(ContextCompat.getColor(requireContext(), R.color.amarilloBtn))
            }
        }

        // Domingo
        val tvDomingoEstado = view.findViewById<TextView>(R.id.tvDomingoEstado)
        val etDomingoDeManana = view.findViewById<EditText>(R.id.etDomingoDeManana)
        val etDomingoAManana = view.findViewById<EditText>(R.id.etDomingoAManana)
        val etDomingoDeTarde = view.findViewById<EditText>(R.id.etDomingoDeTarde)
        val etDomingoATarde = view.findViewById<EditText>(R.id.etDomingoATarde)

        tvDomingoEstado.setOnClickListener {
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

            if (domingoActivo) {
                tvDomingoEstado.text = getString(R.string.textoSubrayadoDeshabilitar)
                tvDomingoEstado.setTextColor(ContextCompat.getColor(requireContext(), R.color.rojoBtn))
            } else {
                tvDomingoEstado.text = getString(R.string.textoSubrayadoHabilitar)
                tvDomingoEstado.setTextColor(ContextCompat.getColor(requireContext(), R.color.amarilloBtn))
            }
        }

        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarConsulta)
        btnGuardar.setOnClickListener {
            val costoStr = etCostoCita.text.toString()
            val costo = String.format("%.2f", costoStr.toDoubleOrNull() ?: 0.0).toDouble()

            val lunesHorarios = listOf(
                etLunesDeManana.text.toString(),
                etLunesAManana.text.toString(),
                etLunesDeTarde.text.toString(),
                etLunesATarde.text.toString()
            )

            val martesHorarios = listOf(
                etMartesDeManana.text.toString(),
                etMartesAManana.text.toString(),
                etMartesDeTarde.text.toString(),
                etMartesATarde.text.toString()
            )

            val miercolesHorarios = listOf(
                etMiercolesDeManana.text.toString(),
                etMiercolesAManana.text.toString(),
                etMiercolesDeTarde.text.toString(),
                etMiercolesATarde.text.toString()
            )

            val juevesHorarios = listOf(
                etJuevesDeManana.text.toString(),
                etJuevesAManana.text.toString(),
                etJuevesDeTarde.text.toString(),
                etJuevesATarde.text.toString()
            )

            val viernesHorarios = listOf(
                etViernesDeManana.text.toString(),
                etViernesAManana.text.toString(),
                etViernesDeTarde.text.toString(),
                etViernesATarde.text.toString()
            )

            val sabadoHorarios = listOf(
                etSabadoDeManana.text.toString(),
                etSabadoAManana.text.toString(),
                etSabadoDeTarde.text.toString(),
                etSabadoATarde.text.toString()
            )

            val domingoHorarios = listOf(
                etDomingoDeManana.text.toString(),
                etDomingoAManana.text.toString(),
                etDomingoDeTarde.text.toString(),
                etDomingoATarde.text.toString()
            )

            val config = ConfiguracionHorario(
                costoCita = costo,
                lunesActivo = lunesActivo,
                lunesHorarios = lunesHorarios,
                martesActivo = martesActivo,
                martesHorarios = martesHorarios,
                miercolesActivo = miercolesActivo,
                miercolesHorarios = miercolesHorarios,
                juevesActivo = juevesActivo,
                juevesHorarios = juevesHorarios,
                viernesActivo = viernesActivo,
                viernesHorarios = viernesHorarios,
                sabadoActivo = sabadoActivo,
                sabadoHorarios = sabadoHorarios,
                domingoActivo = domingoActivo,
                domingoHorarios = domingoHorarios
            )

            // Guardar en Firebase
            val database = FirebaseDatabase.getInstance().getReference("configuracionHorario")

            // obtener id de medico en sesion
            val usuarioId = "usuario123"

            database.child(usuarioId).setValue(config)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Configuración guardada con éxito", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al guardar configuración", Toast.LENGTH_SHORT).show()
                }

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

