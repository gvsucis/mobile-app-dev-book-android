package edu.gvsu.cis.traxy.adapter

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.gvsu.cis.traxy.R
import edu.gvsu.cis.traxy.model.JournalMedia

class PhotoViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val photo: ImageView
    val selected: ImageView
    val view: View

    init {
        view = v
        photo = v.findViewById(R.id.photo)
        selected = v.findViewById(R.id.selected)
    }

    public fun bindTo(m: JournalMedia, listener: ((JournalMedia) -> Unit)?) {
        Glide.with(view).load(m.url).into(photo)
        if (listener != null) {
            photo.setOnClickListener {
                listener(m)
            }
        }
    }
}