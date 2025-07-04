package equipo.dos.citasmedicas.Fragmentos

import Persistencia.sesion
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import Persistencia.ConfiguracionHorario
import Persistencia.Horario
import android.text.Editable
import android.text.TextWatcher
import android.widget.Switch
import com.google.firebase.auth.FirebaseAuth
import equipo.dos.citasmedicas.R

class AjusteConsultaFragment : Fragment() {

    private var lunesManActivo = false
    private var lunesTarActivo = false
    private var martesManActivo = false
    private var martesTarActivo = false
    private var miercolesManActivo = false
    private var miercolesTarActivo = false
    private var juevesManActivo = false
    private var juevesTarActivo = false
    private var viernesManActivo = false
    private var viernesTarActivo = false
    private var sabadoManActivo = false
    private var sabadoTarActivo = false
    private var domingoManActivo = false
    private var domingoTarActivo = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ajuste_consulta, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = sesion.uid.toString()
        val database = FirebaseDatabase.getInstance().getReference("configuracionHorario")

        database.child(userId).get().addOnSuccessListener { snapshot ->
            val configGuardada = snapshot.getValue(ConfiguracionHorario::class.java)
            if (configGuardada != null) {
                inicializarUIConConfiguracion(view, configGuardada)
            } else {
                desactivarTodosLosCampos(view)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "No hay horario configurado", Toast.LENGTH_SHORT).show()
        }

        cargarConfiguracionGuardada { config ->
            if (config != null) {
                activity?.runOnUiThread {
                    inicializarUIConConfiguracion(view, config)
                }
            }
        }

        val etDuracionConsulta = view.findViewById<EditText>(R.id.etDuracionConsulta)
        val etCostoConsulta = view.findViewById<EditText>(R.id.etCostoConsulta)

        etCostoConsulta.addTextChangedListener(object : TextWatcher {
            var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    etCostoConsulta.removeTextChangedListener(this)

                    val clean = s.toString().replace("[^\\d.]".toRegex(), "")
                    current = "$" + clean
                    etCostoConsulta.setText(current)
                    etCostoConsulta.setSelection(current.length)

                    etCostoConsulta.addTextChangedListener(this)
                }
            }
        })

        // Cambio de horario de consulta

        val ids = listOf(
            //mañana
            R.id.etLunesDeManana, R.id.etLunesAManana,
            R.id.etMartesDeManana, R.id.etMartesAManana,
            R.id.etMiercolesDeManana, R.id.etMiercolesAManana,
            R.id.etJuevesDeManana, R.id.etJuevesAManana,
            R.id.etViernesDeManana, R.id.etViernesAManana,
            R.id.etSabadoDeManana, R.id.etSabadoAManana,
            R.id.etDomingoDeManana, R.id.etDomingoAManana,
            //tarde
            R.id.etLunesDeTarde, R.id.etLunesATarde,
            R.id.etMartesDeTarde, R.id.etMartesATarde,
            R.id.etMiercolesDeTarde, R.id.etMiercolesATarde,
            R.id.etJuevesDeTarde, R.id.etJuevesATarde,
            R.id.etViernesDeTarde, R.id.etViernesATarde,
            R.id.etSabadoDeTarde, R.id.etSabadoATarde,
            R.id.etDomingoDeTarde, R.id.etDomingoATarde
        )

        for (id in ids) {
            val editText = view.findViewById<EditText>(id)
            editText.setOnClickListener {
                showTimePicker(editText)
            }
        }

        // Lunes - Mañana
        val swLunesMañanaEstado = view.findViewById<Switch>(R.id.swLunesMañanaEstado)
        val etLunesDeManana = view.findViewById<EditText>(R.id.etLunesDeManana)
        val etLunesAManana = view.findViewById<EditText>(R.id.etLunesAManana)

        swLunesMañanaEstado.setOnCheckedChangeListener { _, isChecked ->
            lunesManActivo = isChecked
            etLunesDeManana.isEnabled = isChecked
            etLunesAManana.isEnabled = isChecked
            val alpha = if (isChecked) 1f else 0.4f
            etLunesDeManana.alpha = alpha
            etLunesAManana.alpha = alpha
        }
        // Lunes - Tarde
        val swLunesTardeEstado = view.findViewById<Switch>(R.id.swLunesTardeEstado)
        val etLunesDeTarde = view.findViewById<EditText>(R.id.etLunesDeTarde)
        val etLunesATarde = view.findViewById<EditText>(R.id.etLunesATarde)

        swLunesTardeEstado.setOnCheckedChangeListener { _, isChecked ->
            lunesTarActivo = isChecked
            etLunesDeTarde.isEnabled = isChecked
            etLunesATarde.isEnabled = isChecked
            val alpha = if (isChecked) 1f else 0.4f
            etLunesDeTarde.alpha = alpha
            etLunesATarde.alpha = alpha
        }

        // Martes - Mañana
        val swMartesMañanaEstado = view.findViewById<Switch>(R.id.swMartesMañanaEstado)
        val etMartesDeManana = view.findViewById<EditText>(R.id.etMartesDeManana)
        val etMartesAManana = view.findViewById<EditText>(R.id.etMartesAManana)

        swMartesMañanaEstado.setOnCheckedChangeListener { _, isChecked ->
            martesManActivo = isChecked
            etMartesDeManana.isEnabled = isChecked
            etMartesAManana.isEnabled = isChecked
            val alpha = if (isChecked) 1f else 0.4f
            etMartesDeManana.alpha = alpha
            etMartesAManana.alpha = alpha
        }

        // Martes - Tarde
        val swMartesTardeEstado = view.findViewById<Switch>(R.id.swMartesTardeEstado)
        val etMartesDeTarde = view.findViewById<EditText>(R.id.etMartesDeTarde)
        val etMartesATarde = view.findViewById<EditText>(R.id.etMartesATarde)

        swMartesTardeEstado.setOnCheckedChangeListener { _, isChecked ->
            martesTarActivo = isChecked
            etMartesDeTarde.isEnabled = isChecked
            etMartesATarde.isEnabled = isChecked
            val alpha = if (isChecked) 1f else 0.4f
            etMartesDeTarde.alpha = alpha
            etMartesATarde.alpha = alpha
        }

        // Miercoles - Mañana
        val swMiercolesMañanaEstado = view.findViewById<Switch>(R.id.swMiercolesMañanaEstado)
        val etMiercolesDeManana = view.findViewById<EditText>(R.id.etMiercolesDeManana)
        val etMiercolesAManana = view.findViewById<EditText>(R.id.etMiercolesAManana)

        swMiercolesMañanaEstado.setOnCheckedChangeListener { _, isChecked ->
            miercolesManActivo = isChecked
            etMiercolesDeManana.isEnabled = isChecked
            etMiercolesAManana.isEnabled = isChecked
            val alpha = if (isChecked) 1f else 0.4f
            etMiercolesDeManana.alpha = alpha
            etMiercolesAManana.alpha = alpha
        }
        // Miercoles - Tarde
        val swMiercolesTardeEstado = view.findViewById<Switch>(R.id.swMiercolesTardeEstado)
        val etMiercolesDeTarde = view.findViewById<EditText>(R.id.etMiercolesDeTarde)
        val etMiercolesATarde = view.findViewById<EditText>(R.id.etMiercolesATarde)

        swMiercolesTardeEstado.setOnCheckedChangeListener { _, isChecked ->
            miercolesTarActivo = isChecked
            etMiercolesDeTarde.isEnabled = isChecked
            etMiercolesATarde.isEnabled = isChecked
            val alpha = if (isChecked) 1f else 0.4f
            etMiercolesDeTarde.alpha = alpha
            etMiercolesATarde.alpha = alpha
        }


        // Jueves - Mañana
        val swJuevesMañanaEstado = view.findViewById<Switch>(R.id.swJuevesMañanaEstado)
        val etJuevesDeManana = view.findViewById<EditText>(R.id.etJuevesDeManana)
        val etJuevesAManana = view.findViewById<EditText>(R.id.etJuevesAManana)

        swJuevesMañanaEstado.setOnCheckedChangeListener { _, isChecked ->
            juevesManActivo = isChecked
            etJuevesDeManana.isEnabled = isChecked
            etJuevesAManana.isEnabled = isChecked
            val alpha = if (isChecked) 1f else 0.4f
            etJuevesDeManana.alpha = alpha
            etJuevesAManana.alpha = alpha
        }
        // Jueves - Tarde
        val swJuevesTardeEstado = view.findViewById<Switch>(R.id.swJuevesTardeEstado)
        val etJuevesDeTarde = view.findViewById<EditText>(R.id.etJuevesDeTarde)
        val etJuevesATarde = view.findViewById<EditText>(R.id.etJuevesATarde)

        swJuevesTardeEstado.setOnCheckedChangeListener { _, isChecked ->
            juevesTarActivo = isChecked
            etJuevesDeTarde.isEnabled = isChecked
            etJuevesATarde.isEnabled = isChecked
            val alpha = if (isChecked) 1f else 0.4f
            etJuevesDeTarde.alpha = alpha
            etJuevesATarde.alpha = alpha
        }

        // Viernes - Mañana
        val swViernesMañanaEstado = view.findViewById<Switch>(R.id.swViernesMañanaEstado)
        val etViernesDeManana = view.findViewById<EditText>(R.id.etViernesDeManana)
        val etViernesAManana = view.findViewById<EditText>(R.id.etViernesAManana)

        swViernesMañanaEstado.setOnCheckedChangeListener { _, isChecked ->
            viernesManActivo = isChecked
            etViernesDeManana.isEnabled = isChecked
            etViernesAManana.isEnabled = isChecked
            val alpha = if (isChecked) 1f else 0.4f
            etViernesDeManana.alpha = alpha
            etViernesAManana.alpha = alpha
        }
        // Viernes - Tarde
        val swViernesTardeEstado = view.findViewById<Switch>(R.id.swViernesTardeEstado)
        val etViernesDeTarde = view.findViewById<EditText>(R.id.etViernesDeTarde)
        val etViernesATarde = view.findViewById<EditText>(R.id.etViernesATarde)

        swViernesTardeEstado.setOnCheckedChangeListener { _, isChecked ->
            viernesTarActivo = isChecked
            etViernesDeTarde.isEnabled = isChecked
            etViernesATarde.isEnabled = isChecked
            val alpha = if (isChecked) 1f else 0.4f
            etViernesDeTarde.alpha = alpha
            etViernesATarde.alpha = alpha
        }


        // Sábado - Mañana
        val swSabadoMañanaEstado = view.findViewById<Switch>(R.id.swSabadoMañanaEstado)
        val etSabadoDeManana = view.findViewById<EditText>(R.id.etSabadoDeManana)
        val etSabadoAManana = view.findViewById<EditText>(R.id.etSabadoAManana)

        swSabadoMañanaEstado.setOnCheckedChangeListener { _, isChecked ->
            sabadoManActivo = isChecked
            etSabadoDeManana.isEnabled = isChecked
            etSabadoAManana.isEnabled = isChecked
            val alpha = if (isChecked) 1f else 0.4f
            etSabadoDeManana.alpha = alpha
            etSabadoAManana.alpha = alpha
        }
        // Sábado - Tarde
        val swSabadoTardeEstado = view.findViewById<Switch>(R.id.swSabadoTardeEstado)
        val etSabadoDeTarde = view.findViewById<EditText>(R.id.etSabadoDeTarde)
        val etSabadoATarde = view.findViewById<EditText>(R.id.etSabadoATarde)

        swSabadoTardeEstado.setOnCheckedChangeListener { _, isChecked ->
            sabadoTarActivo = isChecked
            etSabadoDeTarde.isEnabled = isChecked
            etSabadoATarde.isEnabled = isChecked
            val alpha = if (isChecked) 1f else 0.4f
            etSabadoDeTarde.alpha = alpha
            etSabadoATarde.alpha = alpha
        }


        // Domingo - Mañana
        val swDomingoMañanaEstado = view.findViewById<Switch>(R.id.swDomingoMañanaEstado)
        val etDomingoDeManana = view.findViewById<EditText>(R.id.etDomingoDeManana)
        val etDomingoAManana = view.findViewById<EditText>(R.id.etDomingoAManana)

        swDomingoMañanaEstado.setOnCheckedChangeListener { _, isChecked ->
            domingoManActivo = isChecked
            etDomingoDeManana.isEnabled = isChecked
            etDomingoAManana.isEnabled = isChecked
            val alpha = if (isChecked) 1f else 0.4f
            etDomingoDeManana.alpha = alpha
            etDomingoAManana.alpha = alpha
        }
        // Domingo - Tarde
        val swDomingoTardeEstado = view.findViewById<Switch>(R.id.swDomingoTardeEstado)
        val etDomingoDeTarde = view.findViewById<EditText>(R.id.etDomingoDeTarde)
        val etDomingoATarde = view.findViewById<EditText>(R.id.etDomingoATarde)

        swDomingoTardeEstado.setOnCheckedChangeListener { _, isChecked ->
            domingoTarActivo = isChecked
            etDomingoDeTarde.isEnabled = isChecked
            etDomingoATarde.isEnabled = isChecked
            val alpha = if (isChecked) 1f else 0.4f
            etDomingoDeTarde.alpha = alpha
            etDomingoATarde.alpha = alpha
        }


        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarConsulta)
        btnGuardar.setOnClickListener {

            val costoStr = etCostoConsulta.text.toString().replace("$", "").trim()
            val costo = String.format("%.2f", costoStr.toDoubleOrNull() ?: 0.0).toDouble()

            val duracionStr = etDuracionConsulta.text.toString().trim()
            val duracion = duracionStr.toIntOrNull() ?: 30  // valor por defecto si no escriben nada o no es número


            val config = ConfiguracionHorario(
                costoCita = costo,
                duracionConsulta = duracion,

                lunesMananaActivo = lunesManActivo,
                lunesTardeActivo = lunesTarActivo,
                lunesManana = Horario(
                    desde = etLunesDeManana.text.toString(),
                    hasta = etLunesAManana.text.toString()
                ),
                lunesTarde = Horario(
                    desde = etLunesDeTarde.text.toString(),
                    hasta = etLunesATarde.text.toString()
                ),

                martesMananaActivo = martesManActivo,
                martesTardeActivo = martesTarActivo,
                martesManana = Horario(
                    desde = etMartesDeManana.text.toString(),
                    hasta = etMartesAManana.text.toString()
                ),
                martesTarde = Horario(
                    desde = etMartesDeTarde.text.toString(),
                    hasta = etMartesATarde.text.toString()
                ),

                miercolesMananaActivo = miercolesManActivo,
                miercolesTardeActivo = miercolesTarActivo,
                miercolesManana = Horario(
                    desde = etMiercolesDeManana.text.toString(),
                    hasta = etMiercolesAManana.text.toString()
                ),
                miercolesTarde = Horario(
                    desde = etMiercolesDeTarde.text.toString(),
                    hasta = etMiercolesATarde.text.toString()
                ),

                juevesMananaActivo = juevesManActivo,
                juevesTardeActivo = juevesTarActivo,
                juevesManana = Horario(
                    desde = etJuevesDeManana.text.toString(),
                    hasta = etJuevesAManana.text.toString()
                ),
                juevesTarde = Horario(
                    desde = etJuevesDeTarde.text.toString(),
                    hasta = etJuevesATarde.text.toString()
                ),

                viernesMananaActivo = viernesManActivo,
                viernesTardeActivo = viernesTarActivo,
                viernesManana = Horario(
                    desde = etViernesDeManana.text.toString(),
                    hasta = etViernesAManana.text.toString()
                ),
                viernesTarde = Horario(
                    desde = etViernesDeTarde.text.toString(),
                    hasta = etViernesATarde.text.toString()
                ),

                sabadoMananaActivo = sabadoManActivo,
                sabadoTardeActivo = sabadoTarActivo,
                sabadoManana = Horario(
                    desde = etSabadoDeManana.text.toString(),
                    hasta = etSabadoAManana.text.toString()
                ),
                sabadoTarde = Horario(
                    desde = etSabadoDeTarde.text.toString(),
                    hasta = etSabadoATarde.text.toString()
                ),

                domingoMananaActivo = domingoManActivo,
                domingoTardeActivo = domingoTarActivo,
                domingoManana = Horario(
                    desde = etDomingoDeManana.text.toString(),
                    hasta = etDomingoAManana.text.toString()
                ),
                domingoTarde = Horario(
                    desde = etDomingoDeTarde.text.toString(),
                    hasta = etDomingoATarde.text.toString()
                )
            )

            // Guardar en Firebase
            val database = FirebaseDatabase.getInstance().getReference("configuracionHorario")
            val usuarioId = sesion.uid.toString()

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
        }, hour, minute, false)

        picker.show()
    }



    fun aplicarEstadoInicial(
        sw: Switch,
        etInicio: EditText,
        etFin: EditText,
        activo: Boolean
    ) {
        etInicio.isEnabled = activo
        etFin.isEnabled = activo
        etInicio.alpha = if (activo) 1f else 0.4f
        etFin.alpha = if (activo) 1f else 0.4f
        sw.isChecked = activo  // switch encendido = desactivado
    }

    private fun cargarConfiguracionGuardada(onLoaded: (ConfiguracionHorario?) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            onLoaded(null)
            return
        }
        val ref = FirebaseDatabase.getInstance().getReference("configuracionHorario/$userId")
        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val config = snapshot.getValue(ConfiguracionHorario::class.java)
                onLoaded(config)
            } else {
                onLoaded(null)
            }
        }.addOnFailureListener {
            onLoaded(null)
        }
    }

    private fun inicializarUIConConfiguracion(view: View, config: ConfiguracionHorario) {
        // Costo
        val etCostoCita = view.findViewById<EditText>(R.id.etCostoConsulta)
        etCostoCita.setText(String.format("$%.2f", config.costoCita))
        // Duracion
        val etDuracionConsulta = view.findViewById<EditText>(R.id.etDuracionConsulta)
        etDuracionConsulta.setText(config.duracionConsulta.toString())

        // Lunes
        val swLunesMañanaEstado = view.findViewById<Switch>(R.id.swLunesMañanaEstado)
        val etLunesDeManana = view.findViewById<EditText>(R.id.etLunesDeManana)
        val etLunesAManana = view.findViewById<EditText>(R.id.etLunesAManana)
        val swLunesTardeEstado = view.findViewById<Switch>(R.id.swLunesTardeEstado)
        val etLunesDeTarde = view.findViewById<EditText>(R.id.etLunesDeTarde)
        val etLunesATarde = view.findViewById<EditText>(R.id.etLunesATarde)

        lunesManActivo = config.lunesMananaActivo
        lunesTarActivo = config.lunesTardeActivo
        aplicarEstadoInicial(swLunesMañanaEstado, etLunesDeManana, etLunesAManana, lunesManActivo)
        etLunesDeManana.setText(config.lunesManana.desde)
        etLunesAManana.setText(config.lunesManana.hasta)

        aplicarEstadoInicial(swLunesTardeEstado, etLunesDeTarde, etLunesATarde, lunesTarActivo)
        etLunesDeTarde.setText(config.lunesTarde.desde)
        etLunesATarde.setText(config.lunesTarde.hasta)

        // Martes
        val swMartesMañanaEstado = view.findViewById<Switch>(R.id.swMartesMañanaEstado)
        val etMartesDeManana = view.findViewById<EditText>(R.id.etMartesDeManana)
        val etMartesAManana = view.findViewById<EditText>(R.id.etMartesAManana)
        val swMartesTardeEstado = view.findViewById<Switch>(R.id.swMartesTardeEstado)
        val etMartesDeTarde = view.findViewById<EditText>(R.id.etMartesDeTarde)
        val etMartesATarde = view.findViewById<EditText>(R.id.etMartesATarde)

        martesManActivo = config.martesMananaActivo
        martesTarActivo = config.martesTardeActivo
        aplicarEstadoInicial(swMartesMañanaEstado, etMartesDeManana, etMartesAManana, martesManActivo)
        etMartesDeManana.setText(config.martesManana.desde)
        etMartesAManana.setText(config.martesManana.hasta)

        aplicarEstadoInicial(swMartesTardeEstado, etMartesDeTarde, etMartesATarde, martesTarActivo)
        etMartesDeTarde.setText(config.martesTarde.desde)
        etMartesATarde.setText(config.martesTarde.hasta)

        // Miércoles
        val swMiercolesMañanaEstado = view.findViewById<Switch>(R.id.swMiercolesMañanaEstado)
        val etMiercolesDeManana = view.findViewById<EditText>(R.id.etMiercolesDeManana)
        val etMiercolesAManana = view.findViewById<EditText>(R.id.etMiercolesAManana)
        val swMiercolesTardeEstado = view.findViewById<Switch>(R.id.swMiercolesTardeEstado)
        val etMiercolesDeTarde = view.findViewById<EditText>(R.id.etMiercolesDeTarde)
        val etMiercolesATarde = view.findViewById<EditText>(R.id.etMiercolesATarde)

        miercolesManActivo = config.miercolesMananaActivo
        miercolesTarActivo = config.miercolesTardeActivo
        aplicarEstadoInicial(swMiercolesMañanaEstado, etMiercolesDeManana, etMiercolesAManana, miercolesManActivo)
        etMiercolesDeManana.setText(config.miercolesManana.desde)
        etMiercolesAManana.setText(config.miercolesManana.hasta)

        aplicarEstadoInicial(swMiercolesTardeEstado, etMiercolesDeTarde, etMiercolesATarde, miercolesTarActivo)
        etMiercolesDeTarde.setText(config.miercolesTarde.desde)
        etMiercolesATarde.setText(config.miercolesTarde.hasta)

        // Jueves
        val swJuevesMañanaEstado = view.findViewById<Switch>(R.id.swJuevesMañanaEstado)
        val etJuevesDeManana = view.findViewById<EditText>(R.id.etJuevesDeManana)
        val etJuevesAManana = view.findViewById<EditText>(R.id.etJuevesAManana)
        val swJuevesTardeEstado = view.findViewById<Switch>(R.id.swJuevesTardeEstado)
        val etJuevesDeTarde = view.findViewById<EditText>(R.id.etJuevesDeTarde)
        val etJuevesATarde = view.findViewById<EditText>(R.id.etJuevesATarde)

        juevesManActivo = config.juevesMananaActivo
        juevesTarActivo = config.juevesTardeActivo
        aplicarEstadoInicial(swJuevesMañanaEstado, etJuevesDeManana, etJuevesAManana, juevesManActivo)
        etJuevesDeManana.setText(config.juevesManana.desde)
        etJuevesAManana.setText(config.juevesManana.hasta)

        aplicarEstadoInicial(swJuevesTardeEstado, etJuevesDeTarde, etJuevesATarde, juevesTarActivo)
        etJuevesDeTarde.setText(config.juevesTarde.desde)
        etJuevesATarde.setText(config.juevesTarde.hasta)

        // Viernes
        val swViernesMañanaEstado = view.findViewById<Switch>(R.id.swViernesMañanaEstado)
        val etViernesDeManana = view.findViewById<EditText>(R.id.etViernesDeManana)
        val etViernesAManana = view.findViewById<EditText>(R.id.etViernesAManana)
        val swViernesTardeEstado = view.findViewById<Switch>(R.id.swViernesTardeEstado)
        val etViernesDeTarde = view.findViewById<EditText>(R.id.etViernesDeTarde)
        val etViernesATarde = view.findViewById<EditText>(R.id.etViernesATarde)

        viernesManActivo = config.viernesMananaActivo
        viernesTarActivo = config.viernesTardeActivo
        aplicarEstadoInicial(swViernesMañanaEstado, etViernesDeManana, etViernesAManana, viernesManActivo)
        etViernesDeManana.setText(config.viernesManana.desde)
        etViernesAManana.setText(config.viernesManana.hasta)

        aplicarEstadoInicial(swViernesTardeEstado, etViernesDeTarde, etViernesATarde, viernesTarActivo)
        etViernesDeTarde.setText(config.viernesTarde.desde)
        etViernesATarde.setText(config.viernesTarde.hasta)

        // Sábado
        val swSabadoMañanaEstado = view.findViewById<Switch>(R.id.swSabadoMañanaEstado)
        val etSabadoDeManana = view.findViewById<EditText>(R.id.etSabadoDeManana)
        val etSabadoAManana = view.findViewById<EditText>(R.id.etSabadoAManana)
        val swSabadoTardeEstado = view.findViewById<Switch>(R.id.swSabadoTardeEstado)
        val etSabadoDeTarde = view.findViewById<EditText>(R.id.etSabadoDeTarde)
        val etSabadoATarde = view.findViewById<EditText>(R.id.etSabadoATarde)

        sabadoManActivo = config.sabadoMananaActivo
        sabadoTarActivo = config.sabadoTardeActivo
        aplicarEstadoInicial(swSabadoMañanaEstado, etSabadoDeManana, etSabadoAManana, sabadoManActivo)
        etSabadoDeManana.setText(config.sabadoManana.desde)
        etSabadoAManana.setText(config.sabadoManana.hasta)

        aplicarEstadoInicial(swSabadoTardeEstado, etSabadoDeTarde, etSabadoATarde, sabadoTarActivo)
        etSabadoDeTarde.setText(config.sabadoTarde.desde)
        etSabadoATarde.setText(config.sabadoTarde.hasta)

        // Domingo
        val swDomingoMañanaEstado = view.findViewById<Switch>(R.id.swDomingoMañanaEstado)
        val etDomingoDeManana = view.findViewById<EditText>(R.id.etDomingoDeManana)
        val etDomingoAManana = view.findViewById<EditText>(R.id.etDomingoAManana)
        val swDomingoTardeEstado = view.findViewById<Switch>(R.id.swDomingoTardeEstado)
        val etDomingoDeTarde = view.findViewById<EditText>(R.id.etDomingoDeTarde)
        val etDomingoATarde = view.findViewById<EditText>(R.id.etDomingoATarde)

        domingoManActivo = config.domingoMananaActivo
        domingoTarActivo = config.domingoTardeActivo
        aplicarEstadoInicial(swDomingoMañanaEstado, etDomingoDeManana, etDomingoAManana, domingoManActivo)
        etDomingoDeManana.setText(config.domingoManana.desde)
        etDomingoAManana.setText(config.domingoManana.hasta)

        aplicarEstadoInicial(swDomingoTardeEstado, etDomingoDeTarde, etDomingoATarde, domingoTarActivo)
        etDomingoDeTarde.setText(config.domingoTarde.desde)
        etDomingoATarde.setText(config.domingoTarde.hasta)
    }

    private fun desactivarTodosLosCampos(view: View) {
        val ids = listOf(
            R.id.etLunesDeManana, R.id.etLunesAManana, R.id.etLunesDeTarde, R.id.etLunesATarde,
            R.id.etMartesDeManana, R.id.etMartesAManana, R.id.etMartesDeTarde, R.id.etMartesATarde,
            R.id.etMiercolesDeManana, R.id.etMiercolesAManana, R.id.etMiercolesDeTarde, R.id.etMiercolesATarde,
            R.id.etJuevesDeManana, R.id.etJuevesAManana, R.id.etJuevesDeTarde, R.id.etJuevesATarde,
            R.id.etViernesDeManana, R.id.etViernesAManana, R.id.etViernesDeTarde, R.id.etViernesATarde,
            R.id.etSabadoDeManana, R.id.etSabadoAManana, R.id.etSabadoDeTarde, R.id.etSabadoATarde,
            R.id.etDomingoDeManana, R.id.etDomingoAManana, R.id.etDomingoDeTarde, R.id.etDomingoATarde,
        )

        for (id in ids) {
            val editText = view.findViewById<EditText>(id)
            editText.isEnabled = false
            editText.alpha = 0.4f
        }
    }



}

