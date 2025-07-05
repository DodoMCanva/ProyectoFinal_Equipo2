package Persistencia

import java.io.Serializable

data class medico(
    var uid: String? = null,
    var nombre: String? = null,
    var correo: String? = null,
    var telefono: String? = null,
    var fechaNacimiento: String? = null,
    var genero: String? = null,
    var horario: String? = null,
    var contrasena: String? = null,
    var costoConsulta: Double = 0.0,
    var cedula: String? = null,
    var especialidad: String? = null,
    var direccion: direccion? = null,
    var fotoPerfil: String = ""
) : Serializable {
    constructor() : this("", "", "", "", "", "", "", "", 0.0, "", "", direccion(), "")
}
