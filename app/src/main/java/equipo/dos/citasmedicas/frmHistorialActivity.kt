package equipo.dos.citasmedicas

import modulos.AdapterHistorial
import Persistencia.fakebd
import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
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