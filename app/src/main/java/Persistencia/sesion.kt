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

    fun cargarSesionDesdeFirebase(context: Context) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.uid?.let { uid ->
            val db = FirebaseDatabase.getInstance().getReference("usuarios")
            db.child("medicos").child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val medicoData = snapshot.getValue(medico::class.java)
                            if (medicoData != null) {
                                medicoData.uid = uid
                                asignarSesion(medicoData)
                                Log.d(
                                    "frmPrincipalActivity",
                                    "Sesión de médico recargada: UID = $uid, Foto: ${medicoData.fotoPerfil}"
                                )
                                Log.d(
                                    "PrincipalActivity",
                                    "Sesión global después de recarga: ${(Persistencia.sesion.obtenerSesion() as? medico)?.fotoPerfil}"
                                )
                            } else {
                                Log.e(
                                    "frmPrincipalActivity",
                                    "Error: datos de médico nulos para UID: $uid"
                                )
                                handleSessionError(context)
                            }
                        } else {
                            db.child("pacientes").child(uid)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            val pacienteData =
                                                snapshot.getValue(paciente::class.java)
                                            if (pacienteData != null) {
                                                pacienteData.uid = uid
                                                asignarSesion(pacienteData)

                                                Log.d(
                                                    "frmPrincipalActivity",
                                                    "Sesión de paciente recargada: UID = $uid, Foto: ${pacienteData.fotoPerfil}"
                                                )
                                                Log.d(
                                                    "PrincipalActivity",
                                                    "Sesión global después de recarga: ${(Persistencia.sesion.obtenerSesion() as? paciente)?.fotoPerfil}"
                                                )

                                            } else {
                                                Log.e(
                                                    "frmPrincipalActivity",
                                                    "Error: datos de paciente nulos para UID: $uid"
                                                )
                                                handleSessionError(context)
                                            }
                                        } else {
                                            Log.e(
                                                "frmPrincipalActivity",
                                                "Usuario no encontrado en ninguna rama para UID: $uid"
                                            )
                                            handleSessionError(context)
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.e(
                                            "Firebase",
                                            "Error al leer datos del paciente en frmPrincipal: ${error.message}"
                                        )
                                        handleSessionError(context)
                                    }
                                })
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(
                            "Firebase",
                            "Error al leer datos del médico en frmPrincipal: ${error.message}"
                        )
                        handleSessionError(context)
                    }
                })
        } ?: run {
            Log.e("frmPrincipalActivity", "UID de usuario actual es nulo. Redirigiendo a login.")
            handleSessionError(context)
        }
    }

    private fun handleSessionError(context: Context) {
        cerrarSesion()
        val intent = Intent(context, frmLoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context!!.startActivity(intent)
    }
}







