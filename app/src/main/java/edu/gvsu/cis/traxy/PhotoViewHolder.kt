package edu.gvsu.cis.traxy

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.gvsu.cis.traxy.model.JournalMedia

class PhotoViewHolder(v: View): RecyclerView.ViewHolder(v) {
    val photo: ImageView
    val view:View
    init {
        view = v
        photo = v.findViewById(R.id.photo)
    }

    public fun bindTo(m: JournalMedia) {
        Glide.with(view).load(m.url).into(photo)
    }
}