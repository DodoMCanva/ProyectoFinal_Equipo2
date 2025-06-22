package Persistencia

import java.io.Serializable

data class medico(var nombre : String,
                  var correo : String,
                  var telefono : String,
                  var fechaNacimiento : String,
                  var genero : String,
                  var horario : String,
                  var contrasena: String,
                  var cedula : String,
                  var especialidad : String,
                  var estado : String,
                  var ciudad : String,
                  var calle : String,
                  var numero : String,
                  var cp : String): Serializable
