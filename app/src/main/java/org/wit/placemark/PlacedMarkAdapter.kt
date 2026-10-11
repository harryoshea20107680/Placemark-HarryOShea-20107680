
package org.wit.placemark

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.wit.placemark.models.PlacedMark
import timber.log.Timber

class PlacedMarkAdapter(
    private var marks: List<PlacedMark>,
    private val listener: OnMarkListener
) : RecyclerView.Adapter<PlacedMarkAdapter.MarkViewHolder>() {

    class MarkViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val titleText: TextView =
            itemView.findViewById(R.id.titleText)

        val descriptionText: TextView =
            itemView.findViewById(R.id.descriptionText)

        val coordinatesText: TextView =
            itemView.findViewById(R.id.coordinatesText)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MarkViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_placed_mark,
                parent,
                false
            )

        return MarkViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MarkViewHolder,
        position: Int
    ) {
        val mark = marks[position]

        // Populate the placemark information
        holder.titleText.text =
            "${mark.id}: ${mark.title}"

        holder.descriptionText.text = mark.desc

        holder.coordinatesText.text =
            "X: ${mark.x}, Y: ${mark.y}"

        // Single tap: edit placemark
        holder.itemView.setOnClickListener {
            Timber.d("Mark tapped: id=%d", mark.id)
            listener.onMarkClick(mark)
        }

        // Long press: request placemark deletion
        holder.itemView.setOnLongClickListener {
            Timber.d("Mark long-pressed: id=%d", mark.id)
            listener.onMarkLongClick(mark)
        }
    }

    override fun getItemCount(): Int {
        return marks.size
    }

    fun updateMarks(newMarks: List<PlacedMark>) {
        marks = newMarks.toList()
        notifyDataSetChanged()
    }
}
