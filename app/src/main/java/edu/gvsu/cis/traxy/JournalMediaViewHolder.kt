package edu.gvsu.cis.traxy

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.gvsu.cis.traxy.model.JournalMedia
import edu.gvsu.cis.traxy.model.MediaType

class JournalMediaViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val caption: TextView
    val dateTime: TextView
    val image: ImageView
    val playButton: ImageView
    val editButton: ImageButton
    val view: View

    init {
        view = v
        caption = v.findViewById(R.id.media_caption)
        dateTime = v.findViewById(R.id.media_date_time)
        image = v.findViewById(R.id.media_image)
        playButton = v.findViewById(R.id.play_media)
        editButton = v.findViewById(R.id.edit_button)
    }

    public fun bindTo(m: JournalMedia, listener: ((JournalMedia, String) -> Unit)?) {
        caption.text = m.caption
        dateTime.text = m.date
        when (m.type) {
            MediaType.PHOTO.ordinal -> {
                Glide.with(view).load(m.url).into(image)
                playButton.visibility = View.INVISIBLE
            }
            MediaType.VIDEO.ordinal -> {
                Glide.with(view).load(m.thumbnailUrl).into(image)
                playButton.visibility = View.VISIBLE
            }
            MediaType.AUDIO.ordinal -> {
                playButton.visibility = View.VISIBLE
                image.visibility = View.INVISIBLE
            }
            else -> playButton.visibility = View.INVISIBLE
        }
        if (listener != null) {
            editButton.setOnClickListener {
                listener(m, "EDIT")
            }
            playButton.setOnClickListener {
                listener(m, "VIEW")
            }
            image.setOnClickListener {
                listener(m, "VIEW")
            }
        }
    }
}