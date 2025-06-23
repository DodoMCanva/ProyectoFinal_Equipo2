package Persistencia

import java.io.Serializable

data class paciente(var imagen : String,
                    var nombre : String,
                    var correo : String,
                    var telefono : String,
                    var contrasena : String,
                    var fechaNacimiento : String,
                    var genero : String): Serializable