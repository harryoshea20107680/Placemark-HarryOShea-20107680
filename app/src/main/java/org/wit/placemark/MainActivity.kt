package org.wit.placemark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.pm.ApplicationInfo
import timber.log.Timber
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Timber for debug builds only
        if ((applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0 &&
            Timber.forest().isEmpty()
        ) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.d("MainActivity created")

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Handle system bars and screen padding
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            val padding = (24 * resources.displayMetrics.density).toInt()

            view.setPadding(
                systemBars.left + padding,
                systemBars.top + padding,
                systemBars.right + padding,
                systemBars.bottom + padding
            )

            insets
        }

        // View Marks button
        val viewMarksButton =
            findViewById<Button>(R.id.viewMarksButton)

        // Add Mark button
        val addMarkButton =
            findViewById<Button>(R.id.addMarkButton)

        viewMarksButton.setOnClickListener {
            startActivity(
                Intent(this, MarkListActivity::class.java)
            )
        }

        addMarkButton.setOnClickListener {
            startActivity(
                Intent(this, AddEditActivity::class.java)
            )
        }
    }
}