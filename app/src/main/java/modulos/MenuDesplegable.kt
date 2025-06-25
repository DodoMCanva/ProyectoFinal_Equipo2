package equipo.dos.citasmedicas.helpers

import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.view.View
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import equipo.dos.citasmedicas.*

import java.util.Calendar

object MenuDesplegable {

    fun configurarMenu(activity: Activity) {
        val toolbar = activity.findViewById<Button>(R.id.btnMenu)
        val drawerLayout = activity.findViewById<DrawerLayout>(R.id.drawer)
        val nav = activity.findViewById<NavigationView>(R.id.navegacion_menu)
        val btnAgendar: FloatingActionButton? = activity.findViewById(R.id.btnAgendar)

        toolbar.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val menu = nav.menu
        val opcion = menu.findItem(R.id.btnOpcion)

        if (sesion.tipoSesion() == "paciente") {
            opcion.title = "Agendar"
            opcion.setIcon(R.drawable.date48)
        } else {
            opcion.title = "Ajustar Consulta"
            opcion.setIcon(R.drawable.settings30)
            btnAgendar?.visibility = View.GONE
        }

        nav.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnMenuMisCitas -> {
                    activity.startActivity(Intent(activity, frmPrincipalActivity::class.java))
                }
                R.id.btnMenuHistorial -> {
                    activity.startActivity(Intent(activity, frmHistorialActivity::class.java))
                }
                R.id.btnOpcion -> {
                    val intent = if (sesion.tipoSesion() == "paciente") {
                        Intent(activity, frmAgendarActivity::class.java)
                    } else {
                        Intent(activity, AjustesConsultaActivity::class.java)
                    }
                    activity.startActivity(intent)
                }
                R.id.btnMenuCerrarSesion -> {
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

        val sesionActual = sesion.obtenerSesion()
        val fotoNombre = when (sesionActual) {
            is paciente -> sesionActual.fotoPerfil
            is medico -> sesionActual.fotoPerfil
            else -> null
        }

        fotoNombre?.let {
            val resId = activity.resources.getIdentifier(it, "drawable", activity.packageName)
            if (resId != 0) btnPerfil.setImageResource(resId)
        }

        btnPerfil.setOnClickListener {
            activity.startActivity(Intent(activity, frmMiPerfilActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        btnMenuCerrar.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        val btnCalendario = activity.findViewById<ImageButton?>(R.id.btnCalendarioConsultaCitas)
        val tvFecha = activity.findViewById<TextView?>(R.id.tvConsultaFecha)

        btnCalendario?.setOnClickListener {
            val calendario = Calendar.getInstance()
            val datePicker = DatePickerDialog(activity,
                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    val fechaSeleccionada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    tvFecha?.text = fechaSeleccionada
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        btnAgendar?.setOnClickListener {
            activity.startActivity(Intent(activity, frmAgendarActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }
}