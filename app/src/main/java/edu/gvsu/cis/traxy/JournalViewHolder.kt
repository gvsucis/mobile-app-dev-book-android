package edu.gvsu.cis.traxy

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.gvsu.cis.traxy.model.Header
import edu.gvsu.cis.traxy.model.Journal

class JournalViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val photo: ImageView
    val name: TextView
    val location: TextView
    val date: TextView
    val parentView: View
    val editBtn: Button

    init {
        parentView = v
        photo = v.findViewById(R.id.photo)
        name = v.findViewById(R.id.name)
        location = v.findViewById(R.id.location)
        date = v.findViewById(R.id.date)
        editBtn = v.findViewById(R.id.edit_button)
    }

    public fun bindTo(d: Journal, listener: ((Journal, String) -> Unit)?) {
        if (listener != null) {
            parentView.setOnClickListener {
                listener(d, "VIEW")
            }
            editBtn.setOnClickListener {
                listener(d, "EDIT")
            }
        }
        name.text = d.name;
        location.text = d.address
        date.text = parentView.context.getString(R.string.date_range,
            d.startDate.substring(0, 10),
            d.endDate.substring(0, 10))
        
        d.coverPhotoUrl?.let {
            Glide.with(parentView).load(it).into(photo)
        }


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