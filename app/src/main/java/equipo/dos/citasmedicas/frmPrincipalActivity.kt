package equipo.dos.citasmedicas

import modulos.AdapterCita
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.auth.FirebaseAuth
import Persistencia.cita
import android.util.Log
import Persistencia.sesion
import android.icu.text.DateFormat
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding
import equipo.dos.citasmedicas.helpers.MenuDesplegable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class frmPrincipalActivity : AppCompatActivity() {
    var adapter: AdapterCita? = null
    var f : Boolean = false
    var fecha : String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

    private val binding by lazy {
        ActivityFrmPrincipalBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frm_principal)
        cargarCitas()
        MenuDesplegable.configurarMenu(this)
        val filtro : Switch = findViewById(R.id.swMostrarTodaSemana)
        val calendario : ImageButton = findViewById(R.id.btnCalendarioConsultaCitas)
        val fechaTexto : TextView = findViewById(R.id.tvConsultaFecha)

        calendario.setOnClickListener{
            //se ajusta el calendario pero se tiene que usar otro metodo para la recoleccion
            fecha = fechaTexto.text.toString()
            //Reordenar formato
            cargarCitas()

        }
        filtro.setOnClickListener{
            f = !f
            cargarCitas()
        }

    }

    fun cargarCitas() {
        //Esta linea toma la instancia, para que sea mas modular lo movi a sesion object
        //val uidActual = FirebaseAuth.getInstance().currentUser?.uid
        val tipo = sesion.tipoSesion()


        if (sesion.uid == null) {
            Toast.makeText(this, "Sesión no iniciada.", Toast.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance().getReference("usuarios").child("citas")
        val listaCitas: ListView = findViewById(R.id.lvCitas)

        val query = if (tipo == "paciente") {
            database.orderByChild("idPaciente").equalTo(sesion.uid)
        } else {
            database.orderByChild("idMedico").equalTo(sesion.uid)
        }

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //Aqui la lista se esta recreando, puede ser util para cambio en tiempo real con los hilos
                //pero ahorita solo es mas proceso tedioso
                //mejor cargarla localmente en la sesion
                val citasList = ArrayList<cita>()
                for (citaSnapshot in snapshot.children) {
                    val citaData = citaSnapshot.getValue(Persistencia.cita::class.java)
                    if (citaData != null) {
                        citasList.add(citaData)
                    }
                }
                adapter = AdapterCita(this@frmPrincipalActivity, citasList, tipo, f, fecha)
                listaCitas.adapter = adapter

                if (citasList.isEmpty()) {
                    Toast.makeText(
                        this@frmPrincipalActivity,
                        "No tienes citas agendadas.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al cargar citas: ${error.message}")
                Toast.makeText(
                    this@frmPrincipalActivity,
                    "Error al cargar citas.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}