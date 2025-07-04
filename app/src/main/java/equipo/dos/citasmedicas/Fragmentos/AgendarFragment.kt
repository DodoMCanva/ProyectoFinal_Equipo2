package equipo.dos.citasmedicas.Fragmentos

import Persistencia.medico
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import equipo.dos.citasmedicas.R
import equipo.dos.citasmedicas.frmPrincipalActivity
import modulos.AdapterMedico


class AgendarFragment : Fragment() {
    var adapter: AdapterMedico? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_agendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = FirebaseDatabase.getInstance().getReference("usuarios").child("medicos")
        val listaMedicos: ListView = view.findViewById(R.id.lvMedicos)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val medicosList = ArrayList<medico>()
                for (medicoSnapshot in snapshot.children) {
                    val medicoData = medicoSnapshot.getValue(medico::class.java)
                    if (medicoData != null) {
                        medicoData.uid = medicoSnapshot.key
                        medicosList.add(medicoData)
                    }
                }
                adapter = AdapterMedico(requireContext(), medicosList) { medicoSeleccionado ->
                    val fragment = AgendarMedicoFragment()
                    val bundle = Bundle().apply {
                        putSerializable("medico", medicoSeleccionado)
                    }
                    fragment.arguments = bundle

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.contenedorFragmento, fragment)
                        .addToBackStack(null)
                        .commit()
                }

                listaMedicos.adapter = adapter

                if (medicosList.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "No se encontraron médicos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Error al obtener los médicos.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }
    override fun onResume() {
        super.onResume()
        val tvEncabezado: TextView? = (activity as? frmPrincipalActivity)?.findViewById(R.id.encabezadoPrincipal)
        tvEncabezado?.text = "Agendar"
    }
}
