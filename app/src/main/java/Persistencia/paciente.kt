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


