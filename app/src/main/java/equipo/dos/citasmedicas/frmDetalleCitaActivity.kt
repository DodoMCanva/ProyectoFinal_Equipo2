package equipo.dos.citasmedicas

import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
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

    private val binding by lazy {
        ActivityFrmPrincipalBinding.inflate(layoutInflater)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frm_detalle_cita)

        val nm : TextView = findViewById(R.id.tvMedicoD)
        val esp : TextView = findViewById(R.id.tvEspecialidadD)
        val fecha : TextView = findViewById(R.id.tvFechaD)
        val hora : TextView = findViewById(R.id.tvHoraD)
        val estado : TextView = findViewById(R.id.tvEstadoD)
        val motivo : TextView = findViewById(R.id.tvMotivoD)
        val seccion : LinearLayout = findViewById(R.id.sdr)
        val receta : ImageView = findViewById(R.id.ivRecetaDetallesCita)
        val cancelar : TextView = findViewById(R.id.btnCancelarCita)

        cancelar.setOnClickListener {
            mostrarDialogDeCancelacion()
        }

        nm.text = intent.getStringExtra("nombre")
        esp.text = intent.getStringExtra("especialidad")
        fecha.text = intent.getStringExtra("fecha")
        hora.text = intent.getStringExtra("hora")
        motivo.text = intent.getStringExtra("motivo")

        val estadoCita = intent.getStringExtra("estado")
        estado.text = estadoCita

        when (estadoCita) {
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

        MenuDesplegable.configurarMenu(this)

    }
    private fun mostrarDialogDeCancelacion() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_cancelar_cita)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

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
        val estado: TextView = findViewById(R.id.tvEstadoD)
        val seccion: LinearLayout = findViewById(R.id.sdr)
        val cancelar: TextView = findViewById(R.id.btnCancelarCita)

        estado.text = "Cancelada"
        seccion.visibility = View.GONE
        cancelar.visibility = View.GONE

        Toast.makeText(this, "Cita cancelada", Toast.LENGTH_SHORT).show()
    }
}

