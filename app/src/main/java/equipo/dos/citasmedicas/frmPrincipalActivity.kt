package equipo.dos.citasmedicas

import modulos.AdapterCita
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.auth.FirebaseAuth
import Persistencia.cita
import android.util.Log
import Persistencia.sesion
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding
import equipo.dos.citasmedicas.helpers.MenuDesplegable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//mover a los implements/etc
@RequiresApi(Build.VERSION_CODES.O)
class frmPrincipalActivity : AppCompatActivity() {
    var adapter: AdapterCita? = null
    var filtroBusqueda : Boolean = false
    var fechaBusqueda : String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

    private val binding by lazy {
        ActivityFrmPrincipalBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frm_principal)
        imprimirCitas()
        MenuDesplegable.configurarMenu(this)
        val filtro : Switch = findViewById(R.id.swMostrarTodaSemana)
        val calendario : ImageButton = findViewById(R.id.btnCalendarioConsultaCitas)
        val fechaTexto : TextView = findViewById(R.id.tvConsultaFecha)

        calendario.setOnClickListener{
            fechaBusqueda = fechaTexto.text.toString()
            //Reordenar formato
            imprimirCitas()

        }
        filtro.setOnClickListener{
            filtroBusqueda = !filtroBusqueda
            imprimirCitas()
        }
    }

    fun imprimirCitas() {
        sesion.actualizarListaCitas()
        val listaCitas: ListView = findViewById(R.id.lvCitas)
        if (sesion.citas != null && sesion.citas.isNotEmpty()){
            adapter = AdapterCita(this, sesion.citas, sesion.tipo, filtroBusqueda, fechaBusqueda)
            listaCitas.adapter = adapter
        }
    }
}