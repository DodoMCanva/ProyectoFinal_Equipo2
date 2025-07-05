package equipo.dos.citasmedicas.Fragmentos

import Persistencia.medico
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import equipo.dos.citasmedicas.R
import equipo.dos.citasmedicas.frmPrincipalActivity
import modulos.AdapterCheckEspecialidad
import androidx.core.widget.addTextChangedListener
import modulos.AdapterMedicoFiltrable


class AgendarFragment : Fragment() {
    var adapter: AdapterMedicoFiltrable? = null

    private lateinit var adapterFiltro: AdapterCheckEspecialidad
    private lateinit var listaOriginal: List<medico>
    private lateinit var etBuscar: EditText
    private var textoBusqueda: String = ""
    private var especialidadesSeleccionadas: List<String> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_agendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = FirebaseDatabase.getInstance().getReference("usuarios").child("medicos")

        val btnFiltros = view.findViewById<Button>(R.id.btnFiltros)
        val contenedorFiltros = view.findViewById<View>(R.id.contenedorFiltros)
        val lvEspecialidades = view.findViewById<ListView>(R.id.lvEspecialidades)
        val btnFiltrar = view.findViewById<Button>(R.id.btnFiltrar)
        val lvMedicos = view.findViewById<ListView>(R.id.lvMedicos)
        etBuscar = view.findViewById(R.id.etBuscarMedico)

        // Ocultar/mostrar filtros
        btnFiltros.setOnClickListener {
            contenedorFiltros.visibility =
                if (contenedorFiltros.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

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

                listaOriginal = medicosList
                val especialidadesUnicas = medicosList.mapNotNull { it.especialidad }.toSet().toList()
               // adapterFiltro = AdapterCheckEspecialidad(requireContext(), especialidadesUnicas)

                adapterFiltro = AdapterCheckEspecialidad(requireContext(), especialidadesUnicas).apply {
                    onSeleccionCambio = {
                        aplicarFiltroYBusqueda() // 🔥 Se llama cada que el usuario toca un checkbox
                    }
                }

                lvEspecialidades.adapter = adapterFiltro

                adapter = AdapterMedicoFiltrable(requireContext(), medicosList) { medicoSeleccionado ->
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

                lvMedicos.adapter = adapter

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



        btnFiltrar.setOnClickListener {
            especialidadesSeleccionadas = adapterFiltro.getSeleccionados()  // Guardar selección actual
            contenedorFiltros.visibility = View.GONE
            aplicarFiltroYBusqueda()  // Aplicar filtro combinado
        }

        etBuscar.addTextChangedListener {
            textoBusqueda = it.toString().trim()
            aplicarFiltroYBusqueda()
        }

    }
    override fun onResume() {
        super.onResume()
        val tvEncabezado: TextView? = (activity as? frmPrincipalActivity)?.findViewById(R.id.encabezadoPrincipal)
        tvEncabezado?.text = "Agendar"
    }


    private fun aplicarFiltroYBusqueda() {
        if (!::listaOriginal.isInitialized) return

        val resultado = listaOriginal.filter { medico ->
            val coincideEspecialidad = especialidadesSeleccionadas.isEmpty() || especialidadesSeleccionadas.contains(medico.especialidad)
            val coincideTexto = textoBusqueda.isEmpty() ||
                    (medico.nombre?.contains(textoBusqueda, ignoreCase = true) ?: false) ||
                    (medico.especialidad?.contains(textoBusqueda, ignoreCase = true) ?: false)
            coincideEspecialidad && coincideTexto
        }

        (adapter as? AdapterMedicoFiltrable)?.actualizarLista(resultado)
    }
}


