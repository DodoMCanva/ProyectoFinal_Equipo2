package equipo.dos.citasmedicas

import modulos.AdapterCita
import Persistencia.fakebd
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.auth.FirebaseAuth
import Persistencia.cita
import Persistencia.paciente
import Persistencia.medico
import android.util.Log
import Persistencia.sesion
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding
import equipo.dos.citasmedicas.helpers.MenuDesplegable

class frmPrincipalActivity : AppCompatActivity() {
    var adapter: AdapterCita? = null

    private val binding by lazy {
        ActivityFrmPrincipalBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frm_principal)
        cargarCitas()
        MenuDesplegable.configurarMenu(this)


    }

    fun cargarCitas() {
        val uidActual = FirebaseAuth.getInstance().currentUser?.uid
        val tipo = sesion.tipoSesion()

        if (uidActual == null) {
            Toast.makeText(this, "Sesión no iniciada.", Toast.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance().getReference("usuarios").child("citas")
        val listaCitas: ListView = findViewById(R.id.lvCitas)

        val query = if (tipo == "paciente") {
            database.orderByChild("idPaciente").equalTo(uidActual)
        } else {
            database.orderByChild("idMedico").equalTo(uidActual)
        }

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val citasList = ArrayList<cita>()
                for (citaSnapshot in snapshot.children) {
                    val citaData = citaSnapshot.getValue(cita::class.java)
                    if (citaData != null) {
                        citasList.add(citaData)
                    }
                }
                adapter = AdapterCita(this@frmPrincipalActivity, citasList, tipo, false)
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