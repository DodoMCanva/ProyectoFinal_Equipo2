package Persistencia

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import java.io.Serializable

object sesion {
    var citas: ArrayList<cita> = ArrayList()
    var sesion: Serializable? = null
    var uid: String? = null
    var tipo : String = "no asignado"

    fun asignarSesion(s: Serializable?) {
        sesion = s
        when (sesion) {
            is paciente -> {
                tipo = "paciente"
                val p : paciente = sesion as paciente
                uid = p.uid
            }
            is medico -> {
                tipo = "medico"
                val m : medico = sesion as medico
                uid = m.uid
            }
        }
    }
    fun obtenerSesion(): Serializable? {
        return sesion
    }

    fun tipoSesion():String{
        return tipo
    }

    fun cerrarSesion(){
        FirebaseAuth.getInstance().signOut()
        sesion = null
        tipo = "no asignado"
        uid = null
        citas.clear()
    }
}