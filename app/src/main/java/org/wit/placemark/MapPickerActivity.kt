package org.wit.placemark

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class MapPickerActivity : ComponentActivity() {

    private lateinit var map: MapView
    private var selectedPoint: GeoPoint? = null
    private var marker: Marker? = null
    private var selectedTitle: String = "Selected location"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = packageName
        setContentView(R.layout.activity_map_picker)

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)


// Retrieve the title passed from AddEditActivity
        selectedTitle = intent.getStringExtra("title")
            ?.takeIf { it.isNotBlank() }
            ?: "Selected location"

// Retrieve existing coordinates or use Waterford
        val lat = intent.getDoubleExtra("latitude", 52.245954)
        val lon = intent.getDoubleExtra("longitude", -7.139539)

        val start = GeoPoint(lat, lon)


        map.controller.setZoom(18.0)
        map.controller.setCenter(start)

        setSelectedPoint(start)
        showExistingMarkers()

        // Allow the user to select a point on the map
        val receiver = object : MapEventsReceiver {

            override fun singleTapConfirmedHelper(
                point: GeoPoint
            ): Boolean {
                setSelectedPoint(point)
                return true
            }

            override fun longPressHelper(
                point: GeoPoint
            ): Boolean {
                setSelectedPoint(point)
                return true
            }
        }

        map.overlays.add(MapEventsOverlay(receiver))

        // Return the selected coordinates
        findViewById<Button>(
            R.id.confirmLocationButton
        ).setOnClickListener {

            val point = selectedPoint
                ?: return@setOnClickListener

            val result = Intent()
                .putExtra("latitude", point.latitude)
                .putExtra("longitude", point.longitude)

            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }

    private fun setSelectedPoint(point: GeoPoint) {
        selectedPoint = point

        if (marker == null) {
            marker = Marker(map).apply {

                setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_BOTTOM
                )

                // Use a different icon for the selected location
                icon = androidx.core.content.ContextCompat.getDrawable(
                    this@MapPickerActivity,
                    android.R.drawable.ic_menu_mylocation
                )
            }


            map.overlays.add(marker)
        }

        marker?.position = point
        marker?.title = selectedTitle

        // Display the current selected coordinates
        marker?.snippet =
            "GPS: ${point.latitude}, ${point.longitude}"

        map.invalidate()
    }

    private fun showExistingMarkers() {
        val placemarks = AppData.placemarks.findAll()

        placemarks.forEach { placemark ->
            val point = GeoPoint(
                placemark.x, // Latitude
                placemark.y  // Longitude
            )

            val existingMarker = Marker(map).apply {
                position = point
                title = placemark.title
                snippet = placemark.desc

                setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_BOTTOM
                )
            }

            map.overlays.add(existingMarker)
        }

        map.invalidate()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        map.onPause()
        super.onPause()
    }
}
