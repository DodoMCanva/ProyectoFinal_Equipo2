package Persistencia

data class cita(//Posiblemente se eliminen los primeros dos campos o se cambian
                var paciente : paciente,
                var medico : medico,
                var fecha : String,
                var hora : String,
                var motivo : String,
                var receta : String,
                var estado : String
                )
