package edu.gvsu.cis.traxy

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JournalMediaViewHolder(v: View): RecyclerView.ViewHolder(v) {
    val caption:TextView
    val dateTime:TextView

    init {
        caption = v.findViewById(R.id.media_caption)
        dateTime = v.findViewById(R.id.media_date_time)
    }

    public fun bindTo(m:JournalMedia) {
        caption.text = m.caption
        dateTime.text = m.date
    }
}