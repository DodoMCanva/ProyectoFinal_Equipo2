package Persistencia

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import equipo.dos.citasmedicas.frmLoginActivity
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object sesion {
    var citas: ArrayList<cita> = ArrayList()
    var sesion: Serializable? = null
    var uid: String? = null
    var tipo: String = "no asignado"
    var guardadoEmergente: Serializable? = null

    fun asignarGuardado(s: Serializable) {
        guardadoEmergente = s
    }



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
        citas.clear()
        val database = FirebaseDatabase.getInstance().getReference("usuarios").child("citas")
        val query = if (tipo == "paciente") {
            database.orderByChild("idPaciente").equalTo(uid)
        } else {
            database.orderByChild("idMedico").equalTo(uid)
        }

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (citaSnapshot in snapshot.children) {
                    val citaData = citaSnapshot.getValue(cita::class.java)
                    if (citaData != null) {
                        if (!citas.contains(citaData)) {
                            citas.add(citaData)
                        }
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

    fun buscarMedico(uid: String, callback: (medico?) -> Unit) {
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

    fun listaOrdenada(): ArrayList<cita> {
        val formatoFechaHora = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        var listaOrdenada: List<cita> = citas.sortedWith(compareBy { cita ->
            try {
                val fechaHoraString = "${cita.fecha} ${cita.hora}"

                formatoFechaHora.parse(fechaHoraString)
            } catch (e: Exception) {
                Date(0)
            }
        })
        return ArrayList(listaOrdenada)
    }

    fun marcarCitaCompletada(citaId: String, callback: () -> Unit) {
        val database = FirebaseDatabase.getInstance().getReference("usuarios").child("citas")
        val query = database.orderByKey().equalTo(citaId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val citaData = snapshot.children.first().getValue(cita::class.java)
                    if (citaData != null) {
                        citaData.estado = "Completada"
                        database.child(snapshot.children.first().key!!).setValue(citaData)
                            .addOnSuccessListener {
                                val index = citas.indexOfFirst { it.idCita == citaData.idCita }
                                if (index != -1) {
                                    citas[index] = citaData  // Actualizar la cita en la lista local
                                }
                                callback()
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firebase", "Error al actualizar cita: ${e.message}")
                                callback()
                            }
                    } else {
                        Log.e("Firebase", "Cita no encontrada.")
                        callback()
                    }
                } else {
                    Log.e("Firebase", "No se encontró ninguna cita con ese ID.")
                    callback()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al cargar datos: ${error.message}")
                callback()
            }
        })
    }
}







