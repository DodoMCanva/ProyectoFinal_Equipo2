package equipo.dos.citasmedicas.Fragmentos

import Persistencia.medico
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import android.widget.ImageView
import Persistencia.cita
import Persistencia.paciente
import Persistencia.sesion
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import equipo.dos.citasmedicas.R
import equipo.dos.citasmedicas.frmPrincipalActivity


// Este Fragmento muestra los detalles de una cita médica para un paciente.
// Aqui se cargan los datos de la cita desde Firebase y se muestra la info en pantalla.
// Tambien se puede cancelar la cita si esta pendiente.
class DetalleCitaPacienteFragment : Fragment() {

    // Guardamos el id de la cita q vamos a mostrar
    private var citaId: String = ""

    // Imagen del medico y la receta, si hay
    private lateinit var imgFotoMedicoDetalle: ImageView
    private lateinit var ivRecetaDetallesCitaPaciente: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Aqui se infla la vista del fragmento con su xml
        return inflater.inflate(R.layout.fragment_detalle_cita_paciente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Tratamos de obtener el ID de la cita desde argumentos o la sesion (por si ya estaba guardado)
        citaId = (arguments?.getString("citaId")
            ?: sesion.guardadoEmergente
            ?: savedInstanceState?.getString("citaId")
            ?: "").toString()

        // Si si hay citaId lo guardamos por si se necesita luego
        if (citaId.isNotEmpty()) {
            sesion.asignarGuardado(citaId)
        }

        // Buscamos los elementos de la vista
        imgFotoMedicoDetalle = view.findViewById(R.id.imgFotoPerfil)
        ivRecetaDetallesCitaPaciente = view.findViewById(R.id.ivRecetaDetallesCita)

        // Cargamos los datos de la cita desde la BD
        cargarDatosDeCita(citaId)

        // Boton para cancelar la cita
        val cancelar: Button = view.findViewById(R.id.btnCancelarCita)
        cancelar.setOnClickListener {
            mostrarDialogDeCancelacion()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Guardamos el id de la cita si hay, por si se reinicia la vista
        super.onSaveInstanceState(outState)
        if (citaId.isNotEmpty()) {
            outState.putString("citaId", citaId)
        }
    }

    // Funcion q se conecta a firebase para traer los datos de la cita y mostrarlos
    private fun cargarDatosDeCita(citaId: String) {
        val databaseRef = FirebaseDatabase.getInstance()
            .getReference("usuarios").child("citas").child(citaId)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded) return // pa evitar errores si el fragment ya no esta

                val citaData = snapshot.getValue(cita::class.java)

                if (citaData != null) {
                    val root = requireView()

                    // Obtenemos todos los campos de la vista
                    val nm: TextView = root.findViewById(R.id.tvMedicoD)
                    val esp: TextView = root.findViewById(R.id.tvEspecialidadD)
                    val fecha: TextView = root.findViewById(R.id.tvFechaD)
                    val hora: TextView = root.findViewById(R.id.tvHoraD)
                    val estado: TextView = root.findViewById(R.id.tvEstadoD)
                    val motivo: TextView = root.findViewById(R.id.tvMotivoD)
                    val seccionRecetaPaciente: LinearLayout = root.findViewById(R.id.llSeccionRecetaPaciente)
                    val btnCancelar: Button = root.findViewById(R.id.btnCancelarCita)

                    nm.text = citaData.nombreMedico
                    esp.text = citaData.especialidad
                    fecha.text = citaData.fecha
                    hora.text = citaData.hora
                    motivo.text = citaData.motivo
                    arguments?.let { args ->
                        if (args.getString("origen") == "Historial") {
                            estado.text = args.getString("estado") ?: "Cancelada por Ausencia"
                        } else {
                            estado.text = citaData.estado
                        }
                    } ?: run {
                        estado.text = citaData.estado
                    }

                    if (citaData.estado == "Completada" && !citaData.urlReceta.isNullOrEmpty()) {
                        Glide.with(this@DetalleCitaPacienteFragment)
                            .load(citaData.urlReceta)
                            .placeholder(R.drawable.receta_detalle_cita_medico)
                            .error(R.drawable.receta_detalle_cita_medico)
                            .into(ivRecetaDetallesCitaPaciente)
                        ivRecetaDetallesCitaPaciente.visibility = View.VISIBLE
                    } else {
                        ivRecetaDetallesCitaPaciente.visibility = View.GONE
                    }

                    // Dependiendo del estado de la cita, se muestra o se oculta todo lo relacionado
                    when (citaData.estado) {
                        "Pendiente" -> {
                            seccionRecetaPaciente.visibility = View.GONE
                            btnCancelar.visibility = View.VISIBLE
                            btnCancelar.isEnabled = true
                        }

                        "Completada" -> {
                            seccionRecetaPaciente.visibility = View.VISIBLE
                            btnCancelar.visibility = View.GONE
                            btnCancelar.isEnabled = false
                        }

                        "Cancelada" -> {
                            seccionRecetaPaciente.visibility = View.GONE
                            btnCancelar.visibility = View.GONE
                            btnCancelar.isEnabled = false
                        }

                        "Cancelada por Ausencia" -> {
                            seccionRecetaPaciente.visibility = View.GONE
                            btnCancelar.visibility = View.GONE
                            btnCancelar.isEnabled = false
                        }

                        else -> {
                            seccionRecetaPaciente.visibility = View.GONE
                            btnCancelar.visibility = View.GONE
                            btnCancelar.isEnabled = false
                        }
                    }

                    // Ahora traemos los datos del medico para mostrar la foto
                    val idMedico = citaData.idMedico
                    if (!idMedico.isNullOrEmpty()) {
                        val refMedico = FirebaseDatabase.getInstance()
                            .getReference("usuarios/medicos/$idMedico")
                        refMedico.get().addOnSuccessListener { medicoSnapshot ->
                            val medicoData = medicoSnapshot.getValue(medico::class.java)

                            // si hay foto la cargamos, si no, imagen x default
                            medicoData?.fotoPerfil?.let { fotoUrl ->
                                if (fotoUrl.startsWith("http")) {
                                    Glide.with(this@DetalleCitaPacienteFragment)
                                        .load(fotoUrl)
                                        .placeholder(R.drawable.usuario)
                                        .error(R.drawable.usuario)
                                        .into(imgFotoMedicoDetalle)
                                } else {
                                    imgFotoMedicoDetalle.setImageResource(R.drawable.usuario)
                                }
                            } ?: run {
                                imgFotoMedicoDetalle.setImageResource(R.drawable.usuario)
                            }
                        }.addOnFailureListener {
                            Log.e("Firebase", "Error al cargar datos del médico: ${it.message}")
                            imgFotoMedicoDetalle.setImageResource(R.drawable.usuario)
                        }
                    } else {
                        imgFotoMedicoDetalle.setImageResource(R.drawable.usuario)
                    }

                } else {
                    // Si no se encontro la cita, se avisa
                    Toast.makeText(
                        requireContext(),
                        "Cita no encontrada en la base de datos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Si Firebase falla, mostramos error
                if (!isAdded) return
                context?.let { ctx ->
                    Toast.makeText(ctx, "Error al cargar.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    // Muestra un dialogo para confirmar si realmente se quiere cancelar la cita
    private fun mostrarDialogDeCancelacion() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_cancelar_cita)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val btnConfirmar = dialog.findViewById<Button>(R.id.btnConfirmarCancelacion)
        val btnAtras = dialog.findViewById<Button>(R.id.btnAtrasCancelacion)

        btnConfirmar.setOnClickListener {
            dialog.dismiss()
            actualizarCancelado()
        }

        btnAtras.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // Esta funcion cambia el estado de la cita a Cancelada en firebase
    private fun actualizarCancelado() {
        val databaseRef =
            FirebaseDatabase.getInstance().getReference("usuarios").child("citas").child(citaId)
        val actualizacion = mapOf("estado" to "Cancelada")

        databaseRef.updateChildren(actualizacion)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Cita cancelada con éxito.",
                    Toast.LENGTH_SHORT
                ).show()
                // Recargamos los datos para actualizar la vista
                cargarDatosDeCita(citaId)
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error al cancelar cita: ${it.message}")
                Toast.makeText(requireContext(), "Error al cancelar la cita.", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    // Cuando el fragment se reanuda, cambiamos el encabezado del activity
    override fun onResume() {
        super.onResume()
        val tvEncabezado: TextView? = (activity as? frmPrincipalActivity)?.findViewById(R.id.encabezadoPrincipal)
        tvEncabezado?.text = "Detalle Cita"
    }
}
