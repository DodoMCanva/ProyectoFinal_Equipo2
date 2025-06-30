package Persistencia

import java.io.Serializable

data class cita(
    var idCita: String? = null,
    var idMedico: String? = null,
    var idPaciente: String? = null,

    //esto no deberia de existir
    var nombreMedico: String? = null,
    var nombrePaciente: String? = null,
    var especialidad: String? = null,
    var imagenMedico: String? = null,
    var imagenPaciente: String? = null,

    var fecha: String? = null,
    var hora: String? = null,
    var motivo: String? = null,
    var estado: String? = "Pendiente",
    var receta: String? = null,
    var imagenReceta: String? = null


): Serializable {

    constructor() : this(null, null, null, null, null, null, null, null, "Pendiente", null, null, null, null, null)
}
