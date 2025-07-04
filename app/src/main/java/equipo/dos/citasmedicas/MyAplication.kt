package equipo.dos.citasmedicas

import android.app.Application
import com.cloudinary.android.MediaManager
    class MyAplication: Application() {
    val CLOUD_NAME = "dmapiagtr"


    override fun onCreate() {
        super.onCreate()

        val config = HashMap<String, String>()
        config["cloud_name"] = CLOUD_NAME
        config["secure"] = "true"
        MediaManager.init(this, config)


    }
}