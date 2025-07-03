package equipo.dos.citasmedicas.Fragmentos

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import equipo.dos.citasmedicas.R
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding
import equipo.dos.citasmedicas.helpers.MenuDesplegable


class DetalleCitaPacienteFragment : Fragment() {

    private lateinit var citaId: String


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detalle_cita_paciente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //citaId = intent.getStringExtra("citaId") ?: ""
        if (citaId.isEmpty()) {
            Toast.makeText(requireContext(), "Error: ID de cita no recibido.", Toast.LENGTH_SHORT).show()
          //  finish()
            return
        }

        cargarDatosDeCita(citaId)

        val cancelar: TextView = view.findViewById(R.id.btnCancelarCita)
        cancelar.setOnClickListener {
            mostrarDialogDeCancelacion()
        }

    }

    private fun cargarDatosDeCita(citaId: String) {
        val databaseRef =
            FirebaseDatabase.getInstance().getReference("usuarios").child("citas").child(citaId)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val citaData = snapshot.getValue(Persistencia.cita::class.java)

                if (citaData != null) {
                    val nm: TextView = view!!.findViewById(R.id.tvMedicoD)
                    val esp: TextView = view!!.findViewById(R.id.tvEspecialidadD)
                    val fecha: TextView = view!!.findViewById(R.id.tvFechaD)
                    val hora: TextView = view!!.findViewById(R.id.tvHoraD)
                    val estado: TextView = view!!.findViewById(R.id.tvEstadoD)
                    val motivo: TextView = view!!.findViewById(R.id.tvMotivoD)
                    val seccion: LinearLayout = view!!.findViewById(R.id.sdr)
                    val cancelar: TextView = view!!.findViewById(R.id.btnCancelarCita)

                    nm.text = citaData.nombreMedico
                    esp.text = citaData.especialidad
                    fecha.text = citaData.fecha
                    hora.text = citaData.hora
                    motivo.text = citaData.motivo
                    estado.text = citaData.estado

                    when (citaData.estado) {
                        "Pendiente" -> {
                            seccion.visibility = View.GONE
                            cancelar.visibility = View.VISIBLE
                        }

                        "Completada" -> {
                            seccion.visibility = View.VISIBLE
                            cancelar.visibility = View.GONE
                        }

                        "Cancelada" -> {
                            seccion.visibility = View.GONE
                            cancelar.visibility = View.GONE
                        }

                        else -> {
                            seccion.visibility = View.GONE
                            cancelar.visibility = View.GONE
                        }
                    }

                } else {
                  //  Toast.makeText(
                       // requireContext()@frmDetalleCitaActivity,
                    //    "Cita no encontrada en la base de datos.",
                      //  Toast.LENGTH_SHORT
                 //   ).show()
                    //finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al cargar datos de la cita: ${error.message}")
              //  Toast.makeText(
                   // requireContext()@frmDetalleCitaActivity,
                //    "Error al cargar la cita.",
                    Toast.LENGTH_SHORT
             //   ).show()
             //   finish()
            }
        })
    }

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

    private fun actualizarCancelado() {
        val databaseRef =
            FirebaseDatabase.getInstance().getReference("usuarios").child("citas").child(citaId)
        val actualizacion = mapOf("estado" to "Cancelada")

        databaseRef.updateChildren(actualizacion)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Cita cancelada con éxito en la base de datos.",
                    Toast.LENGTH_SHORT
                ).show()
                cargarDatosDeCita(citaId)
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error al cancelar cita: ${it.message}")
                Toast.makeText(requireContext(), "Error al cancelar la cita.", Toast.LENGTH_SHORT).show()
            }
    }
}