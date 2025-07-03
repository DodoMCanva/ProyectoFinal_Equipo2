package equipo.dos.citasmedicas.Fragmentos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import equipo.dos.citasmedicas.R

class AjusteConsultaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ajuste_consulta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}
