package Persistencia

import java.io.Serializable

object sesion {
    var sesion: Serializable? = null

    fun asignarSesion(s: Serializable?) {
        sesion = s
    }
    fun getSesionPaciente(): Serializable? {
        return sesion
    }
}