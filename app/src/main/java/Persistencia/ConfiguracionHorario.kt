package Persistencia

data class ConfiguracionHorario(
    val costoCita: Double = 0.0,

    val lunesMananaActivo: Boolean = true,
    val lunesTardeActivo: Boolean = true,
    val lunesManana: Horario = Horario(),
    val lunesTarde: Horario = Horario(),

    val martesMananaActivo: Boolean = true,
    val martesTardeActivo: Boolean = true,
    val martesManana: Horario = Horario(),
    val martesTarde: Horario = Horario(),

    val miercolesMananaActivo: Boolean = true,
    val miercolesTardeActivo: Boolean = true,
    val miercolesManana: Horario = Horario(),
    val miercolesTarde: Horario = Horario(),

    val juevesMananaActivo: Boolean = true,
    val juevesTardeActivo: Boolean = true,
    val juevesManana: Horario = Horario(),
    val juevesTarde: Horario = Horario(),

    val viernesMananaActivo: Boolean = true,
    val viernesTardeActivo: Boolean = true,
    val viernesManana: Horario = Horario(),
    val viernesTarde: Horario = Horario(),

    val sabadoMananaActivo: Boolean = true,
    val sabadoTardeActivo: Boolean = true,
    val sabadoManana: Horario = Horario(),
    val sabadoTarde: Horario = Horario(),

    val domingoMananaActivo: Boolean = true,
    val domingoTardeActivo: Boolean = true,
    val domingoManana: Horario = Horario(),
    val domingoTarde: Horario = Horario()
)