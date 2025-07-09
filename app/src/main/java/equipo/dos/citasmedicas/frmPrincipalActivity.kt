package equipo.dos.citasmedicas

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import equipo.dos.citasmedicas.helpers.MenuDesplegable
import Persistencia.sesion
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate

@RequiresApi(Build.VERSION_CODES.O)
class frmPrincipalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        val modoOscuro = prefs.getBoolean("modo_oscuro", false)
        if (modoOscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frm_principal)
        restaurarFragmentoGuardado()
        if (sesion.sesion != null){
            setupUI()
        }
    }


    override fun onResume() {
        super.onResume()
        restaurarFragmentoGuardado()
    }



    private fun setupUI() {
        MenuDesplegable.configurarMenu(this)
        findViewById<com.google.android.material.navigation.NavigationView>(R.id.navegacion_menu).itemIconTintList = null
    }


    private fun restaurarFragmentoGuardado() {
        val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        val fragmentoGuardado = prefs.getString("fragmento_actual", null)

        if (fragmentoGuardado != null) {
            val encabezado = findViewById<TextView>(R.id.encabezadoPrincipal)

            val fragment = when (fragmentoGuardado) {
                "HistorialFragment" -> equipo.dos.citasmedicas.Fragmentos.HistorialFragment().also {
                    encabezado?.text = "Historial"
                }
                "AgendarFragment" -> equipo.dos.citasmedicas.Fragmentos.AgendarFragment().also {
                    encabezado?.text = "Agendar"
                }
                "AjusteConsultaFragment" -> equipo.dos.citasmedicas.Fragmentos.AjusteConsultaFragment().also {
                    encabezado?.text = "Ajustar Consulta"
                }
                "MiPerfilFragment" -> equipo.dos.citasmedicas.Fragmentos.MiPerfilFragment().also {
                    encabezado?.text = "Mi Perfil"
                }
                "AgendarMedicoFragment" -> equipo.dos.citasmedicas.Fragmentos.AgendarMedicoFragment().also {
                    encabezado?.text = "Agendar"
                }
                "DetalleCitaPacienteFragment" -> equipo.dos.citasmedicas.Fragmentos.DetalleCitaPacienteFragment().also {
                    encabezado?.text = "Detalle Cita"
                }
                "DetalleCitaMedicoFragment" -> equipo.dos.citasmedicas.Fragmentos.DetalleCitaMedicoFragment().also {
                    encabezado?.text = "Detalle Cita"
                }
                else -> equipo.dos.citasmedicas.Fragmentos.CitasFragment().also {
                    encabezado?.text = "Mis Citas"
                }
            }

            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.contenedorFragmento, fragment)
            }

            prefs.edit().remove("fragmento_actual").apply()
        }
    }

}