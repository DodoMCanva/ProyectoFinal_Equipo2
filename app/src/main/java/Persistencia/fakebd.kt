package Persistencia

object fakebd {
    val pacientes : ArrayList<paciente> = ArrayList()
    val medicos : ArrayList<medico> = ArrayList()
    val citas : ArrayList<cita> = ArrayList()





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

