package edu.gvsu.cis.traxy

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import edu.gvsu.cis.traxy.model.JournalMedia

class PhotoAdapter(opt: FirestoreRecyclerOptions<JournalMedia>):
    FirestoreRecyclerAdapter<JournalMedia, PhotoViewHolder>(opt) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.photo_item, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int, model: JournalMedia) {
        holder.bindTo(model)
    }
}