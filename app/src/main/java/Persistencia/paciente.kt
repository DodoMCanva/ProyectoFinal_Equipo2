package Persistencia

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.Serializable
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
data class paciente(
    var uid: String? = null,
    var imagen: String? = null,
    var nombre: String?= null,
    var correo: String?= null,
    var telefono: String?= null,
    var contrasena: String?= null,
    var fechaNacimiento: String?= null,
    var genero: String?= null,
    var fotoPerfil: String = "") : Serializable {

    constructor() : this("","", "", "", "", "", "", "", "")

    @RequiresApi(Build.VERSION_CODES.O)
    fun calcularEdad(): Int {
        val posiblesFormatos = listOf(
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )
        return try {
            val fechaNacimiento = posiblesFormatos.firstNotNullOfOrNull { formato ->
                try {
                    LocalDate.parse(this.fechaNacimiento, formato)
                } catch (e: Exception) {
                    null
                }
            }

            if (fechaNacimiento != null) {
                val fechaActual = LocalDate.now()
                Period.between(fechaNacimiento, fechaActual).years
            } else {
                0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

}


