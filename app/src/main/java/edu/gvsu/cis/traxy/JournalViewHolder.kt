package edu.gvsu.cis.traxy

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.gvsu.cis.traxy.model.Header
import edu.gvsu.cis.traxy.model.Journal

class JournalViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val photo: ImageView
    val name: TextView
    val location: TextView
    val parentView: View

    init {
        parentView = v
        photo = v.findViewById(R.id.photo)
        name = v.findViewById(R.id.name)
        location = v.findViewById(R.id.location)
    }

    public fun bindTo(d: Journal, listener: ((Journal) -> Unit)?) {
        if (listener != null)
            parentView.setOnClickListener {
                listener(d)
            }
        name.text = d.name;
        location.text = d.address + " (" +
                d.startDate.substring(0,10) +
                " to " +
                d.endDate.substring(0,10) + ")"
    }
}

class TitleViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val title: TextView

    init {
        title = v.findViewById(R.id.section_title)
    }

    public fun bindTo(h: Header) {
        title.text = h.title
    }
}