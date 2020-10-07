package edu.gvsu.cis.traxy

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class JournalMediaViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val caption: TextView
    val dateTime: TextView
    val image: ImageView
    val playButton: ImageView
    val view: View

    init {
        view = v
        caption = v.findViewById(R.id.media_caption)
        dateTime = v.findViewById(R.id.media_date_time)
        image = v.findViewById(R.id.media_image)
        playButton = v.findViewById(R.id.play_media)
    }

    public fun bindTo(m: JournalMedia) {
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
            else -> playButton.visibility = View.INVISIBLE
        }
    }
}