package equipo.dos.citasmedicas

import modulos.AdapterMedico
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import Persistencia.medico
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import equipo.dos.citasmedicas.databinding.ActivityFrmAgendarBinding
import equipo.dos.citasmedicas.helpers.MenuDesplegable

class frmAgendarActivity : AppCompatActivity() {

    var adapter: AdapterMedico? = null
    private val binding by lazy {
        ActivityFrmAgendarBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frm_agendar)

        MenuDesplegable.configurarMenu(this)

        val database = FirebaseDatabase.getInstance().getReference("usuarios").child("medicos")
        val listaMedicos: ListView = findViewById(R.id.lvMedicos)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val medicosList = ArrayList<medico>()
                for (medicoSnapshot in snapshot.children) {
                    val medicoData = medicoSnapshot.getValue(medico::class.java)
                    if (medicoData != null) {
                        medicoData.uid = medicoSnapshot.key
                        medicosList.add(medicoData)
                    }
                }
                adapter = AdapterMedico(this@frmAgendarActivity, medicosList)
                listaMedicos.adapter = adapter

                if (medicosList.isEmpty()) {
                    Toast.makeText(
                        this@frmAgendarActivity,
                        "No se encontraron médicos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@frmAgendarActivity,
                    "Error al obtener los médicos.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }
}




