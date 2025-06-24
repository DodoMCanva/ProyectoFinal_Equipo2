package Persistencia

import java.io.Serializable

object sesion {
    var sesion: Serializable? = null

    fun asignarSesion(s: Serializable?) {
        sesion = s
    }
    fun obtenerSesion(): Serializable? {
        return sesion
    }

    fun tipoSesion():String{
        return when (sesion) {
            is paciente -> "paciente"
            is medico -> "medico"
            else -> "no asignado"
        }
    }

    fun cerrarSesion(){
        sesion = null
    }
}