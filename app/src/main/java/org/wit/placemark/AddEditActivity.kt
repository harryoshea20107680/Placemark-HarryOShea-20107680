package org.wit.placemark

import android.os.Bundle
import android.text.InputType
import android.view.WindowInsets
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.ComponentActivity
import org.wit.placemark.models.PlacedMark

class AddEditActivity : ComponentActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var xInput: EditText
    private lateinit var yInput: EditText

    private var editingId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editingId = intent.getLongExtra("id", -1L)
            .takeIf { it != -1L }

        createUserInterface()

        editingId?.let { loadExistingMark(it) }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun createUserInterface() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val scrollView = ScrollView(this).apply {
            addView(root)
            setOnApplyWindowInsetsListener { view, insets ->
                val bars = insets.getInsets(
                    WindowInsets.Type.systemBars()
                )
                view.setPadding(
                    bars.left + dp(16),
                    bars.top + dp(16),
                    bars.right + dp(16),
                    bars.bottom + dp(16)
                )
                insets
            }
        }

        titleInput = EditText(this).apply {
            hint = "Title"
        }

        descriptionInput = EditText(this).apply {
            hint = "Description"
        }

        xInput = EditText(this).apply {
            hint = "X coordinate"
            inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL or
                    InputType.TYPE_NUMBER_FLAG_SIGNED
        }

        yInput = EditText(this).apply {
            hint = "Y coordinate"
            inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL or
                    InputType.TYPE_NUMBER_FLAG_SIGNED
        }

        val saveButton = Button(this).apply {
            text = "Save"
            setOnClickListener { saveMark() }
        }

        val cancelButton = Button(this).apply {
            text = "Cancel"
            setOnClickListener { finish() }
        }

        root.addView(titleInput)
        root.addView(descriptionInput)
        root.addView(xInput)
        root.addView(yInput)
        root.addView(saveButton)
        root.addView(cancelButton)

        setContentView(scrollView)
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

        if (title.isEmpty()) {
            titleInput.error = "Title is required"
            return
        }

        val x = xInput.text.toString().toDoubleOrNull()
        if (x == null) {
            xInput.error = "Enter a valid number"
            return
        }

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
            AppData.placemarks.create(mark)
            Toast.makeText(
                this,
                "Mark created",
                Toast.LENGTH_SHORT
            ).show()
        } else {
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