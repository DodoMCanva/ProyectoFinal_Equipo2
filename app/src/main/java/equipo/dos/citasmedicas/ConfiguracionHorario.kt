package equipo.dos.citasmedicas

data class ConfiguracionHorario(
    val costoCita: Double = 0.0,

    val lunesActivo: Boolean = true,
    val lunesHorarios: List<String> = listOf("", "", "", ""),

    val martesActivo: Boolean = true,
    val martesHorarios: List<String> = listOf("", "", "", ""),

    val miercolesActivo: Boolean = true,
    val miercolesHorarios: List<String> = listOf("", "", "", ""),

    val juevesActivo: Boolean = true,
    val juevesHorarios: List<String> = listOf("", "", "", ""),

    val viernesActivo: Boolean = true,
    val viernesHorarios: List<String> = listOf("", "", "", ""),

    val sabadoActivo: Boolean = true,
    val sabadoHorarios: List<String> = listOf("", "", "", ""),

    val domingoActivo: Boolean = true,
    val domingoHorarios: List<String> = listOf("", "", "", "")
)
