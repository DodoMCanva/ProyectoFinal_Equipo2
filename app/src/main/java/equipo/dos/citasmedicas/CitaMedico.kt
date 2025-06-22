package equipo.dos.citasmedicas

data class CitaMedico(
    val fecha: String,
    val hora: String,
    val paciente: String,
    val motivo: String,
    val estado: String
)