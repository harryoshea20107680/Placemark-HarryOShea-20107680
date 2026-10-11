
package org.wit.placemark

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.wit.placemark.models.PlacedMark

class AddEditActivity : ComponentActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var xInput: EditText
    private lateinit var yInput: EditText

    private var editingId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load the XML layout
        setContentView(R.layout.activity_add_edit)

        // Handle system bar insets
        val root = findViewById<View>(R.id.addEditRoot)

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                bars.left,
                bars.top,
                bars.right,
                bars.bottom
            )

            insets
        }

        // Connect Kotlin variables to XML fields
        titleInput = findViewById(R.id.titleInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        xInput = findViewById(R.id.xInput)
        yInput = findViewById(R.id.yInput)

        val saveButton = findViewById<Button>(R.id.saveButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        // Check whether we are editing an existing mark
        editingId = intent.getLongExtra("id", -1L)
            .takeIf { it != -1L }

        if (editingId != null) {
            findViewById<TextView>(R.id.formTitle)
                .text = "Edit Mark"

            loadExistingMark(editingId!!)
        }

        saveButton.setOnClickListener {
            saveMark()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun loadExistingMark(id: Long) {
        val mark = AppData.placemarks.findOne(id)

        if (mark == null) {
            Toast.makeText(
                this,
                "Mark not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        titleInput.setText(mark.title)
        descriptionInput.setText(mark.desc)
        xInput.setText(mark.x.toString())
        yInput.setText(mark.y.toString())
    }

    private fun saveMark() {
        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()

        // Validate title
        if (title.isEmpty()) {
            titleInput.error = "Title is required"
            return
        }

        // Validate X coordinate
        val x = xInput.text.toString().toDoubleOrNull()

        if (x == null) {
            xInput.error = "Enter a valid number"
            return
        }

        // Validate Y coordinate
        val y = yInput.text.toString().toDoubleOrNull()

        if (y == null) {
            yInput.error = "Enter a valid number"
            return
        }

        val mark = PlacedMark(
            id = editingId ?: 0L,
            title = title,
            desc = description,
            x = x,
            y = y
        )

        if (editingId == null) {
            // Create a new placemark
            AppData.placemarks.create(mark)

            Toast.makeText(
                this,
                "Mark created",
                Toast.LENGTH_SHORT
            ).show()

        } else {
            // Update an existing placemark
            val updated = AppData.placemarks.update(mark)

            if (!updated) {
                Toast.makeText(
                    this,
                    "Mark not found",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            Toast.makeText(
                this,
                "Mark updated",
                Toast.LENGTH_SHORT
            ).show()
        }

        finish()
    }
}
