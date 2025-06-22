package equipo.dos.citasmedicas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import equipo.dos.citasmedicas.databinding.ActivityFrmEditarBinding

class frmEditarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFrmEditarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFrmEditarBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}