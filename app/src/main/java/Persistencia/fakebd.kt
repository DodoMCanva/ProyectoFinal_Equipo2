package Persistencia

object fakebd {
    val pacientes : ArrayList<paciente> = ArrayList()
    val medicos : ArrayList<medico> = ArrayList()
    val citas : ArrayList<cita> = ArrayList()


    init {
        pacientes.add(paciente("ruta", "Cesar", "cesard@gmail.com", "612220781", "123456", "14-07-2002", "Masculino"));
        pacientes.add(paciente("ruta", "Ana", "ana_lopez@gmail.com", "664112233", "ana123", "23-03-1995", "Femenino"));
        pacientes.add(paciente("ruta", "Luis", "luis_mendez@hotmail.com", "618334455", "luispass", "09-11-1988", "Masculino"));
        pacientes.add(paciente("ruta", "Carla", "carlamz@yahoo.com", "656778899", "carla456", "30-01-2000", "Femenino"));
        pacientes.add(paciente("ruta", "Jorge", "jorge.rivera@gmail.com", "622556677", "jorgepass", "17-06-1992", "Masculino"));

        medicos.add(medico("Fernando", "fer@gmail.com", "6645565", "1990-01-15", "Femenino", "12/5/40", "fer12", "C887DJJ3", "Cardiologo", "Sonora", "Obregon", "Viena", "2037", "35180"));
        medicos.add(medico("Laura", "laura.med@gmail.com", "6654433", "1985-07-22", "Femenino", "14/7/38", "lau85", "K123LMN7", "Pediatra", "Jalisco", "Guadalajara", "Colon", "1021", "44160"));
        medicos.add(medico("Marco", "marco.ortiz@hotmail.com", "6677788", "1978-03-10", "Masculino", "10/3/40", "marco78", "M987POQ9", "Dermatologo", "CDMX", "CDMX", "Insurgentes", "305", "03100"));
        medicos.add(medico("Paola", "paola.garcia@gmail.com", "6688991", "1992-12-01", "Femenino", "01/12/44", "pao92", "L554XYZ2", "Ginecologo", "Nuevo Leon", "Monterrey", "Garza Sada", "100", "64700"));
        medicos.add(medico("Ricardo", "ricardo.medico@gmail.com", "6690002", "1980-05-30", "Masculino", "30/5/39", "rick80", "H321QWE8", "Neurologo", "Baja California", "Tijuana", "Reforma", "250", "22000"));

        //ruta no la modifiques para todos y estado puede ser Pendiente, Completada y Cancelada
        citas.add(cita(pacientes[0], medicos[0], "2023-07-14", "12:00", "Sangrado anal", "ruta", "Pendiente"))
        citas.add(cita(pacientes[1], medicos[1], "2023-08-23", "14:30", "Revisión de niño", "ruta", "Pendiente"))
        citas.add(cita(pacientes[2], medicos[2], "2023-09-10", "10:00", "Consulta dermatológica", "ruta", "Pendiente"))
        citas.add(cita(pacientes[3], medicos[3], "2023-10-05", "16:00", "Chequeo ginecológico", "ruta", "Completada"))
        citas.add(cita(pacientes[4], medicos[4], "2023-11-22", "11:00", "Chequeo neurológico", "ruta", "Pendiente"))
    }

    fun addPaciente(p: paciente) {
        pacientes.add(p)
    }

    fun addMedico(m: medico) {
        medicos.add(m)
    }

    fun addCita(c: cita) {
        citas.add(c)
    }
}

