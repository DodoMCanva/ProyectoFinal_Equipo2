package Persistencia

import android.content.Context
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import equipo.dos.citasmedicas.R
import modulos.AdapterCita
import java.io.Serializable

object sesion {
    var citas: ArrayList<cita> = ArrayList()
    var sesion: Serializable? = null
    var uid: String? = null
    var tipo: String = "no asignado"

    fun asignarSesion(s: Serializable?) {
        sesion = s
        when (sesion) {
            is paciente -> {
                tipo = "paciente"
                val p: paciente = sesion as paciente
                uid = p.uid
            }

            is medico -> {
                tipo = "medico"
                val m: medico = sesion as medico
                uid = m.uid
            }
        }
    }

    fun obtenerSesion(): Serializable? {
        return sesion
    }

    fun actualizarListaCitas() {
        val database = FirebaseDatabase.getInstance().getReference("usuarios").child("citas")
        val query = if (tipo == "paciente") {
            database.orderByChild("idPaciente").equalTo(Persistencia.sesion.uid)
        } else {
            database.orderByChild("idMedico").equalTo(Persistencia.sesion.uid)
        }

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (citaSnapshot in snapshot.children) {
                    val citaData = citaSnapshot.getValue(Persistencia.cita::class.java)
                    if (citaData != null) {
                        citas.add(citaData)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al cargar citas: ${error.message}")
            }
        })
    }

    fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        sesion = null
        tipo = "no asignado"
        uid = null
        citas.clear()
    }
}


