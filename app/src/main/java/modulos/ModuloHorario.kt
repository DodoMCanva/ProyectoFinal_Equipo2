package modulos

import Persistencia.ConfiguracionHorario
import Persistencia.Horario
import Persistencia.cita
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

    fun generarHorasEnHorario(desde: String, hasta: String): List<String> {
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
                horas.add(horaFormateada)

                calendarDesde.add(Calendar.MINUTE, 30)
            }
        } catch (e: Exception) {
            return horas
        }

        return horas
    }

    fun obtenerConfiguracionDelMedico(uidMedico: String, onResultado: (ConfiguracionHorario?) -> Unit) {
        val database = FirebaseDatabase.getInstance().getReference("configuracionHorario").child(uidMedico)
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

    fun validarDiaConsulta(uidMedico: String, fecha: String, hora: String, onResultado: (Boolean) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("usuarios").child("citas")
        val query = ref.orderByChild("idMedico").equalTo(uidMedico)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (citaSnapshot in snapshot.children) {
                    val citaExistente = citaSnapshot.getValue(cita::class.java)
                    if (citaExistente != null && citaExistente.fecha == fecha && citaExistente.hora == hora) {
                        onResultado(false)
                        return
                    }
                }
                onResultado(true)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al validar consulta: ${error.message}")
                onResultado(false)
            }
        })
    }


}