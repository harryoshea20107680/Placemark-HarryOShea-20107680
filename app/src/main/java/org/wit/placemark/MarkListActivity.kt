
package org.wit.placemark

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.wit.placemark.models.PlacedMark

class MarkListActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlacedMarkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mark_list)

        // Apply system bar insets while keeping layout padding
        val root = findViewById<View>(R.id.markListRoot)

        val left = root.paddingLeft
        val top = root.paddingTop
        val right = root.paddingRight
        val bottom = root.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                left + bars.left,
                top + bars.top,
                right + bars.right,
                bottom + bars.bottom
            )

            insets
        }

        // Find RecyclerView in the XML layout
        recyclerView = findViewById(R.id.marksRecyclerView)

        // Create adapter using our existing placemark store
        adapter = PlacedMarkAdapter(
            marks = AppData.placemarks.findAll().toList(),

            onEdit = { mark ->
                editMark(mark)
            },

            onDelete = { mark ->
                deleteMark(mark)
            }
        )

        // Configure RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Back to Menu button
        val returnButton =
            findViewById<Button>(R.id.returnButton)

        returnButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {
            adapter.updateMarks(
                AppData.placemarks.findAll().toList()
            )
        }
    }

    private fun editMark(mark: PlacedMark) {
        val intent = Intent(
            this,
            AddEditActivity::class.java
        )

        intent.putExtra("id", mark.id)

        startActivity(intent)
    }

    private fun deleteMark(mark: PlacedMark) {
        AppData.placemarks.delete(mark.id)

        adapter.updateMarks(
            AppData.placemarks.findAll().toList()
        )
    }
}
