package equipo.dos.citasmedicas.Fragmentos

import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import android.widget.Toast
import equipo.dos.citasmedicas.R
import equipo.dos.citasmedicas.frmEditarActivity
import equipo.dos.citasmedicas.frmLoginActivity
import equipo.dos.citasmedicas.frmPrincipalActivity
import equipo.dos.citasmedicas.helpers.MenuDesplegable

class MiPerfilFragment : Fragment() {

    private lateinit var imgFotoPerfil: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mi_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imgFotoPerfil = view.findViewById(R.id.imgFotoPerfil)

        var p: paciente? = null
        var m: medico? = null

        val nombre: TextView = view.findViewById(R.id.perfilNombre)
        val numero: TextView = view.findViewById(R.id.perfilNumero)
        val fecha: TextView = view.findViewById(R.id.perfilFechaNa)
        val genero: TextView = view.findViewById(R.id.perfilGenero)
        val cerrar: TextView = view.findViewById(R.id.btnCerrarSesion)
        val editar: TextView = view.findViewById(R.id.btnEditarPerfil)

        Log.d("MiPerfilFragment", "Cargando perfil. Foto URL en sesion global: ${(Persistencia.sesion.obtenerSesion() as? medico)?.fotoPerfil ?: (Persistencia.sesion.obtenerSesion() as? paciente)?.fotoPerfil}")
        when (val s = sesion.obtenerSesion()) {
            is paciente -> {
                nombre.text = s.nombre
                numero.text = s.correo
                fecha.text = s.fechaNacimiento
                genero.text = s.genero

                Glide.with(this)
                    .load(s.fotoPerfil)
                    .placeholder(R.drawable.usuario)
                    .error(R.drawable.usuario)
                    .into(imgFotoPerfil)

            }

            is medico -> {
                nombre.text = s.nombre
                numero.text = s.correo
                fecha.text = s.fechaNacimiento
                genero.text = s.genero
                Glide.with(this)
                    .load(s.fotoPerfil)
                    .placeholder(R.drawable.usuario)
                    .error(R.drawable.usuario)
                    .into(imgFotoPerfil)
                Log.d("MiPerfilFragment", "Cargando perfil. Foto URL en sesion global: ${(Persistencia.sesion.obtenerSesion() as? medico)?.fotoPerfil ?: (Persistencia.sesion.obtenerSesion() as? paciente)?.fotoPerfil}")

            }

            else -> {
                Toast.makeText(requireContext(), "No se cargó correctamente la sesión", Toast.LENGTH_SHORT).show()
                imgFotoPerfil.setImageResource(R.drawable.usuario)
            }
        }

        editar.setOnClickListener() {
            var inte: Intent = Intent(requireContext(), frmEditarActivity::class.java)
            startActivity(inte)
        }
        cerrar.setOnClickListener() {
            val inte = Intent(requireContext(), frmLoginActivity::class.java)
            inte.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            sesion.cerrarSesion()
            startActivity(inte)
        }

    }
    override fun onResume() {
        super.onResume()
        val tvEncabezado: TextView? = (activity as? frmPrincipalActivity)?.findViewById(R.id.encabezadoPrincipal)
        tvEncabezado?.text = "Mi Perfil"
    }
}
