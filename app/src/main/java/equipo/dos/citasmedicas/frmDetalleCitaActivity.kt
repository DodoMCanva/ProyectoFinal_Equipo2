package equipo.dos.citasmedicas

import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding
import equipo.dos.citasmedicas.helpers.MenuDesplegable

class frmDetalleCitaActivity : AppCompatActivity() {
    private lateinit var citaId: String
    private val binding by lazy {
        ActivityFrmPrincipalBinding.inflate(layoutInflater)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_detalle_cita)

        citaId = intent.getStringExtra("citaId") ?: ""
        if (citaId.isEmpty()) {
            Toast.makeText(this, "Error: ID de cita no recibido.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDatosDeCita(citaId)

        val cancelar: TextView = findViewById(R.id.btnCancelarCita)
        cancelar.setOnClickListener {
            mostrarDialogDeCancelacion()
        }

        MenuDesplegable.configurarMenu(this)
    }

    private fun cargarDatosDeCita(citaId: String) {
        val databaseRef =
            FirebaseDatabase.getInstance().getReference("usuarios").child("citas").child(citaId)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val citaData = snapshot.getValue(Persistencia.cita::class.java)

                if (citaData != null) {
                    val nm: TextView = findViewById(R.id.tvMedicoD)
                    val esp: TextView = findViewById(R.id.tvEspecialidadD)
                    val fecha: TextView = findViewById(R.id.tvFechaD)
                    val hora: TextView = findViewById(R.id.tvHoraD)
                    val estado: TextView = findViewById(R.id.tvEstadoD)
                    val motivo: TextView = findViewById(R.id.tvMotivoD)
                    val seccion: LinearLayout = findViewById(R.id.sdr)
                    val cancelar: TextView = findViewById(R.id.btnCancelarCita)

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
                    Toast.makeText(
                        this@frmDetalleCitaActivity,
                        "Cita no encontrada en la base de datos.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al cargar datos de la cita: ${error.message}")
                Toast.makeText(
                    this@frmDetalleCitaActivity,
                    "Error al cargar la cita.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        })
    }

    private fun mostrarDialogDeCancelacion() {
        val dialog = Dialog(this)
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
                    this,
                    "Cita cancelada con éxito en la base de datos.",
                    Toast.LENGTH_SHORT
                ).show()
                cargarDatosDeCita(citaId)
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error al cancelar cita: ${it.message}")
                Toast.makeText(this, "Error al cancelar la cita.", Toast.LENGTH_SHORT).show()
            }
    }
}