package org.wit.placemark

import android.app.Application
import android.content.pm.ApplicationInfo
import org.wit.placemark.models.PlacemarkJSONStore
import timber.log.Timber

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if ((applicationInfo.flags and
                    ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            Timber.plant(Timber.DebugTree())
        }

        AppData.placemarks =
            PlacemarkJSONStore(applicationContext)

        Timber.i("Placemark application started")
    }
}
