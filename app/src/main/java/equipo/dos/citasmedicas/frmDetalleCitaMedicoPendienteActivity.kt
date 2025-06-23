package equipo.dos.citasmedicas

import Persistencia.medico
import Persistencia.paciente
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
import com.google.android.material.navigation.NavigationView
import equipo.dos.citasmedicas.databinding.ActivityFrmDetalleCitaMedicoPendienteBinding
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding

class frmDetalleCitaMedicoPendienteActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityFrmDetalleCitaMedicoPendienteBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)


        val campoNombre: TextView = findViewById(R.id.tvPacienteDetalleCitaMedico)
        val campoEdad: TextView = findViewById(R.id.tvEdadDetalleCitaMedico)
        val campoGenero: TextView = findViewById(R.id.tvGeneroDetalleCitaMedico)
        val campoTelefono: TextView = findViewById(R.id.tvTelefonoDetalleCitaMedico)
        val campoFecha: TextView = findViewById(R.id.tvFechaDetalleCitaMedico)
        val campoHora: TextView = findViewById(R.id.tvHoraDetalleCitaMedico)
        val campoEstado: TextView = findViewById(R.id.tvEstadoDetalleCitaMedico)
        val campoMotivo: TextView = findViewById(R.id.tvMotivoDetalleCitaMedico)

        val seccionReceta: LinearLayout = findViewById(R.id.llSeccionRecteaDetalleCita)
        val seccionBotones: LinearLayout = findViewById(R.id.llSeccionOpcionesDetallesCita)

        var estado = intent.getStringExtra("estado")
        when (estado) {

            "Pendiente" -> {
                seccionReceta.visibility = View.INVISIBLE
            }

            "Completada" -> {
                seccionBotones.visibility = View.INVISIBLE
            }

            "Cancelada" -> {
                seccionBotones.visibility = View.INVISIBLE
                seccionReceta.visibility = View.INVISIBLE
            }

            else -> Toast.makeText(this, "Se mando", Toast.LENGTH_SHORT).show()
        }

        campoNombre.setText(intent.getStringExtra("nombre"))
        campoEdad.setText(intent.getStringExtra("edad"))
        campoGenero.setText(intent.getStringExtra("genero"))
        campoTelefono.setText(intent.getStringExtra("telefono"))
        campoFecha.setText(intent.getStringExtra("fecha"))
        campoHora.setText(intent.getStringExtra("hora"))
        campoEstado.setText(intent.getStringExtra("estado"))
        campoMotivo.setText(intent.getStringExtra("motivo"))

        val sesion = intent.getSerializableExtra("sesion")
        val tipoSesion: String = when (sesion) {
            is paciente -> "paciente"
            is medico -> "medico"
            else -> "no asignado"
        }

        val drawerLayout = binding.drawerDetalleCitaMedico
        val toolbar =
            binding.btnMenu
        val nav =
            binding.navegacionMenu

        toolbar.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val menu = nav.menu
        val opcion = menu.findItem(R.id.btnMenuOpcion)

        if (tipoSesion == "paciente") {
            opcion.setIcon(R.drawable.date48)
            opcion.title = "Agendar"
        } else {
            opcion.setIcon(R.drawable.settings30)
            opcion.title = "Ajustes de Consulta"
        }

        nav.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnMenuMisCitas -> {
                    Toast.makeText(this, "Mis citas", Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.btnMenuOpcion -> {
                    val inte: Intent
                    if (tipoSesion == "paciente") {
                        inte = Intent(this, frmAgendarActivity::class.java)
                    } else {
                        inte = Intent(this, AjustesConsultaActivity::class.java)
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                    startActivity(inte)
                    true
                }

                R.id.btnMenuCerrarSesion -> {
                    Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                else -> false
            }
        }
        val headerView = nav.getHeaderView(0)

        val btnPerfil = headerView.findViewById<ImageView>(R.id.btnPerfil)
        val btnMenuCerrar = headerView.findViewById<Button>(R.id.btnMenuCerrarMenu)

        btnPerfil.setOnClickListener {
            val inte = Intent(this, frmMiPerfilActivity::class.java)
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(inte)
        }

        btnMenuCerrar.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        binding.btnFinalizarDetallesCitaMedico.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_subir_receta)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.show()
        }

        binding.btnReprogramarDetallesMedico.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_reprogramar_cita)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.show()
        }

        binding.btnCancelarDetallesMedico.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_cancelar_cita)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.show()
        }

    }
}
