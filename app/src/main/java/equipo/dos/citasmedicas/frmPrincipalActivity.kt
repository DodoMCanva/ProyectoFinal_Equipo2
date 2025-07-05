package equipo.dos.citasmedicas

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import equipo.dos.citasmedicas.Fragmentos.CitasFragment
import equipo.dos.citasmedicas.databinding.ActivityFrmPrincipalBinding
import equipo.dos.citasmedicas.helpers.MenuDesplegable
import Persistencia.medico
import Persistencia.paciente
import Persistencia.sesion

@RequiresApi(Build.VERSION_CODES.O)
class frmPrincipalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frm_principal)
        if (sesion.sesion != null){
            setupUI()
        }else{
            cargarSesionDesdeFirebase()
        }
    }

    override fun onResume() {
        super.onResume()

        cargarSesionDesdeFirebase()
    }

    private fun cargarSesionDesdeFirebase() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.uid?.let { uid ->
            val db = FirebaseDatabase.getInstance().getReference("usuarios")

            db.child("medicos").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val medicoData = snapshot.getValue(medico::class.java)
                        if (medicoData != null) {
                            medicoData.uid = uid
                            sesion.asignarSesion(medicoData)
                            Log.d("frmPrincipalActivity", "Sesión de médico recargada: UID = $uid, Foto: ${medicoData.fotoPerfil}")
                            Log.d("PrincipalActivity", "Sesión global después de recarga: ${(Persistencia.sesion.obtenerSesion() as? medico)?.fotoPerfil}")
                            setupUI()
                        } else {
                            Log.e("frmPrincipalActivity", "Error: datos de médico nulos para UID: $uid")
                            handleSessionError()
                        }
                    } else {
                        db.child("pacientes").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val pacienteData = snapshot.getValue(paciente::class.java)
                                    if (pacienteData != null) {
                                        pacienteData.uid = uid
                                        Persistencia.sesion.asignarSesion(pacienteData)

                                        Log.d("frmPrincipalActivity", "Sesión de paciente recargada: UID = $uid, Foto: ${pacienteData.fotoPerfil}")
                                        Log.d("PrincipalActivity", "Sesión global después de recarga: ${(Persistencia.sesion.obtenerSesion() as? paciente)?.fotoPerfil}")

                                        setupUI()
                                    } else {
                                        Log.e("frmPrincipalActivity", "Error: datos de paciente nulos para UID: $uid")
                                        handleSessionError()
                                    }
                                } else {
                                    Log.e("frmPrincipalActivity", "Usuario no encontrado en ninguna rama para UID: $uid")
                                    handleSessionError()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Firebase", "Error al leer datos del paciente en frmPrincipal: ${error.message}")
                                handleSessionError()
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error al leer datos del médico en frmPrincipal: ${error.message}")
                    handleSessionError()
                }
            })
        } ?: run {
            Log.e("frmPrincipalActivity", "UID de usuario actual es nulo. Redirigiendo a login.")
            handleSessionError()
        }
    }

    private fun setupUI() {
        MenuDesplegable.configurarMenu(this)
        findViewById<com.google.android.material.navigation.NavigationView>(R.id.navegacion_menu).itemIconTintList = null

        if (this is frmPrincipalActivity) {
            this.supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.contenedorFragmento, CitasFragment())
                addToBackStack(null)
            }
        }
    }

    private fun handleSessionError() {
        Toast.makeText(this, "Error al cargar tu perfil. Por favor, inicia sesión de nuevo.", Toast.LENGTH_LONG).show()
        sesion.cerrarSesion()
        val intent = Intent(this, frmLoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}