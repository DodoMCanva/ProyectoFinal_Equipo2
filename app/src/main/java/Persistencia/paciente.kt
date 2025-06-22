package Persistencia

import java.io.Serializable

data class paciente(var nombre : String,
                    var correo : String,
                    var telefono : String,
                    var contrasena : String,
                    var fechaNacimiento : String,
                    var genero : String): Serializable