package equipo.dos.citasmedicas

import modulos.AdapterMedico
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
import equipo.dos.citasmedicas.databinding.ActivityFrmAgendarBinding
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding
import equipo.dos.citasmedicas.helpers.MenuDesplegable

class frmAgendarActivity : AppCompatActivity() {

    var adapter: AdapterMedico?= null
    private val binding by lazy {
        ActivityFrmAgendarBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frm_agendar)

        MenuDesplegable.configurarMenu(this)

        //adapter
        adapter = AdapterMedico(this, fakebd.medicos)
        var listaMedicos: ListView= findViewById(R.id.lvMedicos)
        listaMedicos.adapter=adapter



    }
}




