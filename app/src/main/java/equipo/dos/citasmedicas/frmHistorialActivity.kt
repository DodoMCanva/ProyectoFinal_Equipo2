package equipo.dos.citasmedicas

import modulos.AdapterHistorial
import Persistencia.sesion
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import equipo.dos.citasmedicas.databinding.ActivityFrmHistorialBinding
import equipo.dos.citasmedicas.helpers.MenuDesplegable

class frmHistorialActivity : AppCompatActivity() {
    var adapter: AdapterHistorial? = null

    private val binding by lazy {
        ActivityFrmHistorialBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frm_historial)
        adapter = AdapterHistorial(this, sesion.citas, sesion.tipo)
        var listaCitas = findViewById<ListView>(R.id.lvHistorial)
        listaCitas.adapter = adapter

        MenuDesplegable.configurarMenu(this)
    }
}