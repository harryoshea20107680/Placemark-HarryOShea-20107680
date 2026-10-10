package org.wit.placemark

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.WindowInsets
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity

class PlacemarkActivity : ComponentActivity() {

    private lateinit var listLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createUserInterface()
    }

    override fun onResume() {
        super.onResume()

        if (::listLayout.isInitialized) {
            displayMarks()
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun createUserInterface() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
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

        val title = TextView(this).apply {
            text = "Placed Marks"
            textSize = 28f
            gravity = Gravity.CENTER
        }

        val addButton = Button(this).apply {
            text = "Add Mark"
            setOnClickListener {
                val intent = Intent(
                    this@PlacemarkActivity,
                    AddEditActivity::class.java
                )
                startActivity(intent)
            }
        }

        listLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val scrollView = ScrollView(this).apply {
            addView(listLayout)
        }

        root.addView(title)
        root.addView(addButton)

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
        displayMarks()
    }

    private fun displayMarks() {
        listLayout.removeAllViews()

        val marks = AppData.placemarks.findAll()

        if (marks.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "No placed marks yet."
                textSize = 18f
                setPadding(0, dp(20), 0, dp(20))
            }

            listLayout.addView(emptyText)
            return
        }

        for (mark in marks) {
            val markLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, dp(12), 0, dp(12))
            }

            val markTitle = TextView(this).apply {
                text = "${mark.id}: ${mark.title}"
                textSize = 20f
            }

            val markDescription = TextView(this).apply {
                text = mark.desc
                textSize = 16f
            }

            val coordinates = TextView(this).apply {
                text = "X: ${mark.x}, Y: ${mark.y}"
                textSize = 14f
            }

            val editButton = Button(this).apply {
                text = "Edit"
                setOnClickListener {
                    val intent = Intent(
                        this@PlacemarkActivity,
                        AddEditActivity::class.java
                    )
                    intent.putExtra("id", mark.id)
                    startActivity(intent)
                }
            }

            val deleteButton = Button(this).apply {
                text = "Delete"
                setOnClickListener {
                    AppData.placemarks.delete(mark.id)
                    displayMarks()
                }
            }

            markLayout.addView(markTitle)
            markLayout.addView(markDescription)
            markLayout.addView(coordinates)
            markLayout.addView(editButton)
            markLayout.addView(deleteButton)

            listLayout.addView(markLayout)
        }
    }
}