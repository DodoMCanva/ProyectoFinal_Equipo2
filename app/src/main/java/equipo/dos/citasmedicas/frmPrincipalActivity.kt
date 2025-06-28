package equipo.dos.citasmedicas

import modulos.AdapterCita
import Persistencia.fakebd
import Persistencia.sesion
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding
import equipo.dos.citasmedicas.helpers.MenuDesplegable

class frmPrincipalActivity : AppCompatActivity() {

    var adapter: AdapterCita? = null

    private val binding by lazy {
        ActivityFrmPrincipalBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frm_principal)
        cargarCitas()
        MenuDesplegable.configurarMenu(this)


    }

    fun cargarCitas() {
        adapter = AdapterCita(this, fakebd.citas, sesion.tipoSesion(), false)
        var listaCitas: ListView = findViewById(R.id.lvCitas)
        listaCitas.adapter = adapter
    }


}
