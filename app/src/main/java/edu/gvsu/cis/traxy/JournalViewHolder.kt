package edu.gvsu.cis.traxy

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JournalViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val photo: ImageView
    val name: TextView
    val location: TextView

    init {
        photo = v.findViewById(R.id.photo)
        name = v.findViewById(R.id.name)
        location = v.findViewById(R.id.location)
    }

    public fun bindTo(d: Journal) {
        name.text = d.name;
        location.text = d.location + " (" +
                d.startDate.toLocalDate().toString() +
                " to " +
                d.endDate.toLocalDate().toString() + ")"
    }
}