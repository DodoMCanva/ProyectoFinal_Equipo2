package Persistencia

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    fun actualizarListaCitas(callback: () -> Unit) {
        citas = ArrayList<cita>()
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
                callback()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al cargar citas: ${error.message}")
                callback()
            }
        })
    }

    fun buscarPaciente(uid: String, callback: (paciente?) -> Unit) {
        val db = FirebaseDatabase.getInstance().getReference("usuarios/pacientes/$uid")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pacienteData = snapshot.getValue(paciente::class.java)
                pacienteData?.uid = uid
                callback(pacienteData)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al leer datos del paciente: ${error.message}")
                callback(null)
            }
        })
    }

    fun buscarMedico(uid: String, callback: (medico?) -> Unit){
        val db = FirebaseDatabase.getInstance().getReference("usuarios/medicos/$uid")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val medicoData = snapshot.getValue(medico::class.java)
                medicoData?.uid = uid
                callback(medicoData)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al leer datos del paciente: ${error.message}")
                callback(null)
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
    fun listaOrdenada(): ArrayList<cita>{
        val formatoFechaHora = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val listaOrdenada = citas.sortedWith(compareBy { cita ->
            try {
                val fechaHoraString = "${cita.fecha} ${cita.hora}"

                formatoFechaHora.parse(fechaHoraString)
            } catch (e: Exception) {
                Date(0)
            }
        })
        return listaOrdenada as ArrayList<cita>
            }


    }



