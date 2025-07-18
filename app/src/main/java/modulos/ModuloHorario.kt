package modulos

import Persistencia.ConfiguracionHorario
import Persistencia.Horario
import Persistencia.cita
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
@RequiresApi(Build.VERSION_CODES.O)
class ModuloHorario {

    fun obtenerConfigDelDia(config: ConfiguracionHorario, dayOfWeek: Int): Pair<Pair<Boolean, Horario>, Pair<Boolean, Horario>> {
        return when (dayOfWeek) {
            Calendar.MONDAY -> Pair(
                Pair(config.lunesMananaActivo, config.lunesManana),
                Pair(config.lunesTardeActivo, config.lunesTarde)
            )
            Calendar.TUESDAY -> Pair(
                Pair(config.martesMananaActivo, config.martesManana),
                Pair(config.martesTardeActivo, config.martesTarde)
            )
            Calendar.WEDNESDAY -> Pair(
                Pair(config.miercolesMananaActivo, config.miercolesManana),
                Pair(config.miercolesTardeActivo, config.miercolesTarde)
            )
            Calendar.THURSDAY -> Pair(
                Pair(config.juevesMananaActivo, config.juevesManana),
                Pair(config.juevesTardeActivo, config.juevesTarde)
            )
            Calendar.FRIDAY -> Pair(
                Pair(config.viernesMananaActivo, config.viernesManana),
                Pair(config.viernesTardeActivo, config.viernesTarde)
            )
            Calendar.SATURDAY -> Pair(
                Pair(config.sabadoMananaActivo, config.sabadoManana),
                Pair(config.sabadoTardeActivo, config.sabadoTarde)
            )
            Calendar.SUNDAY -> Pair(
                Pair(config.domingoMananaActivo, config.domingoManana),
                Pair(config.domingoTardeActivo, config.domingoTarde)
            )
            else -> Pair(
                Pair(false, Horario()),
                Pair(false, Horario())
            )
        }
    }

    fun listaInhabiles(config : ConfiguracionHorario) : ArrayList<Int> {
        var lista = ArrayList<Int>()
        if (!config.lunesMananaActivo && !config.lunesTardeActivo) {
            lista.add(Calendar.MONDAY)
        }
        if (!config.martesMananaActivo && !config.martesTardeActivo) {
            lista.add(Calendar.TUESDAY)
        }
        if (!config.miercolesMananaActivo && !config.miercolesTardeActivo) {
            lista.add(Calendar.WEDNESDAY)
        }
        if (!config.juevesMananaActivo && !config.juevesTardeActivo) {
            lista.add(Calendar.THURSDAY)
        }
        if (!config.viernesMananaActivo && !config.viernesTardeActivo) {
            lista.add(Calendar.FRIDAY)
        }
        if (!config.sabadoMananaActivo && !config.sabadoTardeActivo) {
            lista.add(Calendar.SATURDAY)
        }
        if (!config.domingoMananaActivo && !config.domingoTardeActivo) {
            lista.add(Calendar.SUNDAY)
        }
        return lista
    }

    fun generarHorasEnHorario(desde: String, hasta: String, salto : Int): List<String> {
        val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())
        val horas = mutableListOf<String>()

        val calendarDesde = Calendar.getInstance()
        val calendarHasta = Calendar.getInstance()

        try {
            val fechaDesde = formatoHora.parse(desde)
            val fechaHasta = formatoHora.parse(hasta)
            if (fechaDesde == null || fechaHasta == null) return horas

            calendarDesde.time = fechaDesde
            calendarHasta.time = fechaHasta

            while (calendarDesde.before(calendarHasta) || calendarDesde == calendarHasta) {
                val hora = calendarDesde.get(Calendar.HOUR_OF_DAY)
                val minuto = calendarDesde.get(Calendar.MINUTE)

                val amPm = if (hora < 12) "AM" else "PM"
                val hora12 = if (hora % 12 == 0) 12 else hora % 12
                val horaFormateada = String.format("%d:%02d %s", hora12, minuto, amPm)
                val tempCalendar = calendarDesde.clone() as Calendar
                tempCalendar.add(Calendar.MINUTE, salto)
                if (!tempCalendar.after(calendarHasta)) {
                    horas.add(horaFormateada)
                    calendarDesde.add(Calendar.MINUTE, salto)
                } else {
                    break
                }
            }
        } catch (e: Exception) {
            return horas
        }
        return horas
    }

    fun obtenerConfiguracionDelMedico(uidMedico: String, onResultado: (ConfiguracionHorario?) -> Unit) {
        val database = FirebaseDatabase.getInstance().getReference("usuarios").child("medicos").child(uidMedico).child("configuracionHorario")
        database.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val config = snapshot.getValue(ConfiguracionHorario::class.java)
                onResultado(config)
            } else {
                onResultado(null)
            }
        }.addOnFailureListener {
            onResultado(null)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validarDiaConsulta(uidMedico: String, fecha: String, hora: String, onResultado: (Boolean) -> Unit) {
        try {
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaConsulta = formato.parse(fecha) ?: run {
                onResultado(false)
                return
            }

            val fechaActual = formato.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))

            val calendario = Calendar.getInstance()
            calendario.time = fechaActual!!
            calendario.add(Calendar.YEAR, 2)
            val  fechaLimite = calendario.time

            if (fechaConsulta.before(fechaActual) || fechaConsulta.after(fechaLimite)) {
                onResultado(false)
                return
            }

        } catch (e: Exception) {
            onResultado(false)
            return
        }
        val ref = FirebaseDatabase.getInstance()
            .getReference("usuarios")
            .child("citas")

        val query = ref.orderByChild("idMedico").equalTo(uidMedico)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (citaSnap in snapshot.children) {
                    val c = citaSnap.getValue(cita::class.java)
                    if (c != null && c.fecha == fecha && c.hora == hora ) {
                        onResultado(false)
                        return
                    }
                }
                onResultado(true)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error validando cita: ${error.message}")
                onResultado(false)
            }
        })
    }

    fun eliminarHorasOcupadas(
        uidMedico: String,
        fecha: String,
        horasDisponibles: MutableList<String>,
        onResultado: (MutableList<String>) -> Unit
    ) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("usuarios")
            .child("citas")

        val query = ref.orderByChild("idMedico").equalTo(uidMedico)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val horasOcupadas = mutableListOf<LocalTime>()
                val formatoHora = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)

                for (citaSnap in snapshot.children) {
                    val cita = citaSnap.getValue(cita::class.java)
                    if (cita != null && cita.fecha == fecha) {
                        try {
                            val horaCita = LocalTime.parse(cita.hora?.trim(), formatoHora)
                            horasOcupadas.add(horaCita)
                        } catch (e: Exception) {
                            Log.e("ParseError", "Hora inválida en cita: '${cita.hora}'")
                        }
                    }
                }

                val hoy = LocalDate.now()
                val ahora = LocalTime.now()
                val fechaConsulta = try {
                    LocalDate.parse(fecha, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                } catch (e: Exception) {
                    null
                }

                val horasRestantes = horasDisponibles.filter { horaStr ->
                    try {
                        val hora = LocalTime.parse(horaStr.trim(), formatoHora)

                        val esFutura = if (fechaConsulta != null && fechaConsulta.isEqual(hoy)) {
                            hora.isAfter(ahora)
                        } else {
                            true
                        }

                        hora !in horasOcupadas && esFutura
                    } catch (e: Exception) {
                        Log.e("ParseError", "Error al parsear hora disponible: '$horaStr'")
                        false
                    }
                }.toMutableList()

                onResultado(horasRestantes)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al eliminar horas ocupadas: ${error.message}")
                onResultado(horasDisponibles)
            }
        })
    }


}
