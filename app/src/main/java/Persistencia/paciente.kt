package Persistencia

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.Serializable
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
data class paciente(
    var imagen: String,
    var nombre: String,
    var correo: String,
    var telefono: String,
    var contrasena: String,
    var fechaNacimiento: String,
    var genero: String
) : Serializable {


    @RequiresApi(Build.VERSION_CODES.O)
    fun calcularEdad(): Int {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return try {
            val fechaNacimiento = LocalDate.parse(this.fechaNacimiento, formatter)
            val fechaActual = LocalDate.now()
            Period.between(fechaNacimiento, fechaActual).years
        } catch (e: DateTimeParseException) {
            e.printStackTrace()
            0
        }
    }

}


