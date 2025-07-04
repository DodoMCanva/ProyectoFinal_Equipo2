package Persistencia

import java.io.Serializable

data class direccion(
    var estado: String = "",
    var ciudad: String = "",
    var calle: String = "",
    var numero: String = "",
    var cp: String = ""
) : Serializable