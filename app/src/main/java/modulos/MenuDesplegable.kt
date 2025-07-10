package equipo.dos.citasmedicas.helpers

import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.util.Log
import com.bumptech.glide.Glide
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.commit
import com.google.android.material.navigation.NavigationView
import equipo.dos.citasmedicas.*
import equipo.dos.citasmedicas.Fragmentos.AgendarFragment
import equipo.dos.citasmedicas.Fragmentos.AgendarMedicoFragment
import equipo.dos.citasmedicas.Fragmentos.AjusteConsultaFragment
import equipo.dos.citasmedicas.Fragmentos.CitasFragment
import equipo.dos.citasmedicas.Fragmentos.DetalleCitaMedicoFragment
import equipo.dos.citasmedicas.Fragmentos.DetalleCitaPacienteFragment
import equipo.dos.citasmedicas.Fragmentos.HistorialFragment
import equipo.dos.citasmedicas.Fragmentos.MiPerfilFragment

import java.util.Calendar

object MenuDesplegable {

    @RequiresApi(Build.VERSION_CODES.O)
    fun configurarMenu(activity: Activity) {
        val toolbar = activity.findViewById<Button>(R.id.btnMenu)
        val drawerLayout = activity.findViewById<DrawerLayout>(R.id.drawer)
        val nav = activity.findViewById<NavigationView>(R.id.navegacion_menu)
        val encabezado: TextView = activity.findViewById(R.id.encabezadoPrincipal)


        toolbar.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val menu = nav.menu

        val opcion = menu.findItem(R.id.btnOpcion)

        if (sesion.tipo == "paciente") {
            opcion.title = "Agendar"
            opcion.setIcon(R.drawable.date48)
        } else {
            opcion.title = "Ajustar Consulta"
            opcion.setIcon(R.drawable.settings30)
        }

        nav.setNavigationItemSelectedListener { item ->
            val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(activity)

            when (item.itemId) {
                R.id.btnMenuMisCitas -> {
                    prefs.edit().putString("fragmento_actual", "CitasFragment").apply()

                    if (activity is frmPrincipalActivity) {
                        activity.supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            replace(R.id.contenedorFragmento, CitasFragment())
                            addToBackStack(null)
                            encabezado.setText("Mis Citas")
                        }
                    }
                }

                R.id.btnMenuHistorial -> {
                    prefs.edit().putString("fragmento_actual", "HistorialFragment").apply()

                    if (activity is frmPrincipalActivity) {
                        activity.supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            replace(R.id.contenedorFragmento, HistorialFragment())
                            addToBackStack(null)
                            encabezado.setText("Historial")
                        }
                    }
                }

                R.id.btnOpcion -> {
                    if (sesion.tipo == "paciente") {
                        prefs.edit().putString("fragmento_actual", "AgendarFragment").apply()

                        if (activity is frmPrincipalActivity) {
                            activity.supportFragmentManager.commit {
                                setReorderingAllowed(true)
                                replace(R.id.contenedorFragmento, AgendarFragment())
                                addToBackStack(null)
                                encabezado.setText("Agendar")
                            }
                        }
                    } else {
                        prefs.edit().putString("fragmento_actual", "AjusteConsultaFragment").apply()

                        if (activity is frmPrincipalActivity) {
                            activity.supportFragmentManager.commit {
                                setReorderingAllowed(true)
                                replace(R.id.contenedorFragmento, AjusteConsultaFragment())
                                addToBackStack(null)
                                encabezado.setText("Ajustar Consulta")
                            }
                        }
                    }
                }

                R.id.btnMenuCerrarSesion -> {
                    prefs.edit().remove("fragmento_actual").apply()
                    sesion.cerrarSesion()
                    activity.startActivity(Intent(activity, frmLoginActivity::class.java))
                }
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val headerView = nav.getHeaderView(0)
        val btnPerfil = headerView.findViewById<ImageView>(R.id.btnPerfil)
        val btnMenuCerrar = headerView.findViewById<Button>(R.id.btnMenuCerrarMenu)





        val switchModoOscuro = headerView.findViewById<Switch>(R.id.swModoOscuro)

        // Obtener SharedPreferences
        val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(activity)
        val modoOscuroActivado = prefs.getBoolean("modo_oscuro", false)
        switchModoOscuro.isChecked = modoOscuroActivado

        switchModoOscuro.setOnCheckedChangeListener { _, isChecked ->
            val fragmentoActual = obtenerNombreDelFragmentoActual(activity)
            prefs.edit()
                .putBoolean("modo_oscuro", isChecked)
                .putString("fragmento_actual", fragmentoActual)
                .apply()

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val sesionActual = sesion.obtenerSesion()
        Log.d("MenuDesplegable", "Configurando menú. Foto URL en sesion global: ${(sesionActual as? medico)?.fotoPerfil ?: (sesionActual as? paciente)?.fotoPerfil}")

        val fotoUrl = when (sesionActual) {
            is paciente -> sesionActual.fotoPerfil
            is medico -> sesionActual.fotoPerfil
            else -> null
        }

        fotoUrl?.let {
            Glide.with(activity)
                .load(it) // Carga la URL de la foto de perfil (que viene de Cloudinary)
                .placeholder(R.drawable.usuario) // Imagen por defecto mientras carga
                .error(R.drawable.usuario)     // Imagen por defecto si hay un error al cargar
                .into(btnPerfil)
            Log.d("MenuDesplegable", "Glide cargando foto: $it")
        } ?: run {
            Log.d("MenuDesplegable", "No hay URL de foto, mostrando placeholder.")
            btnPerfil.setImageResource(R.drawable.usuario)
        }

        btnPerfil.setOnClickListener {
            val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(activity)
            prefs.edit().putString("fragmento_actual", "MiPerfilFragment").apply()

            if (activity is frmPrincipalActivity) {
                activity.supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(R.id.contenedorFragmento, MiPerfilFragment())
                    addToBackStack(null)
                    encabezado.setText("Mi Perfil")
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
        }


        btnMenuCerrar.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        val btnCalendario = activity.findViewById<ImageButton?>(R.id.btnCalendarioConsultaCitas)
        val tvFecha = activity.findViewById<TextView?>(R.id.tvConsultaFecha)

        btnCalendario?.setOnClickListener {
            val calendario = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                activity,
                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    val fechaSeleccionada =
                        String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    tvFecha?.text = fechaSeleccionada
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }


    }

    private fun obtenerNombreDelFragmentoActual(activity: Activity): String {
        val fragment = (activity as? frmPrincipalActivity)
            ?.supportFragmentManager
            ?.findFragmentById(R.id.contenedorFragmento)

        return when (fragment) {
            is CitasFragment -> "CitasFragment"
            is HistorialFragment -> "HistorialFragment"
            is AgendarFragment -> "AgendarFragment"
            //descomentarear si se quiere implementar
            //is AgendarMedicoFragment -> "AgendarMedicoFragment"
            is AjusteConsultaFragment -> "AjusteConsultaFragment"
            is MiPerfilFragment -> "MiPerfilFragment"
            //descomentarear si se quiere implementar
            //is DetalleCitaPacienteFragment -> "DetalleCitaPacienteFragment"
            //is DetalleCitaMedicoFragment -> "DetalleCitaMedicoFragment"
            else -> "CitasFragment"
        }
    }
}