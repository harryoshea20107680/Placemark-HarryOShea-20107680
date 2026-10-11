
package org.wit.placemark

import android.app.AlertDialog
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
import timber.log.Timber

class MarkListActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlacedMarkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mark_list)

        // Handle system bar insets
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

        // Find RecyclerView
        recyclerView = findViewById(R.id.marksRecyclerView)

        // Create adapter using our new OnMarkListener interface
        adapter = PlacedMarkAdapter(
            marks = AppData.placemarks.findAll().toList(),

            listener = object : OnMarkListener {

                override fun onMarkClick(mark: PlacedMark) {
                    editMark(mark)
                }

                override fun onMarkLongClick(mark: PlacedMark): Boolean {
                    confirmDelete(mark)
                    return true
                }
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

    // Open selected placemark for editing
    private fun editMark(mark: PlacedMark) {

        Timber.d("Opening editor for mark id=%d", mark.id)

        val intent = Intent(
            this,
            AddEditActivity::class.java
        )

        intent.putExtra("id", mark.id)

        startActivity(intent)
    }

    // Ask for confirmation before deleting
    private fun confirmDelete(mark: PlacedMark) {

        Timber.d(
            "Showing delete confirmation for mark id=%d",
            mark.id
        )

        AlertDialog.Builder(this)
            .setTitle("Delete Placemark")
            .setMessage(
                "Are you sure you want to delete '${mark.title}'?"
            )
            .setPositiveButton("Delete") { _, _ ->
                deleteMark(mark)
            }
            .setNegativeButton("Cancel") { _, _ ->
                Timber.d(
                    "Deletion cancelled for mark id=%d",
                    mark.id
                )
            }
            .show()
    }
    // Delete placemark and refresh RecyclerView
    private fun deleteMark(mark: PlacedMark) {

        val deleted = AppData.placemarks.delete(mark.id)

        if (deleted) {
            Timber.i("Mark deleted: id=%d", mark.id)
        } else {
            Timber.w("Could not delete mark: id=%d", mark.id)
        }

        adapter.updateMarks(
            AppData.placemarks.findAll().toList()
        )
    }
}
