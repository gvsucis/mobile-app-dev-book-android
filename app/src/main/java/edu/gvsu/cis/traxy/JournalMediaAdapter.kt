package edu.gvsu.cis.traxy

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class JournalMediaAdapter(opt:FirestoreRecyclerOptions<JournalMedia>):
    FirestoreRecyclerAdapter<JournalMedia,JournalMediaViewHolder>(opt) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalMediaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.media_item, parent, false)
        return JournalMediaViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: JournalMediaViewHolder,
        position: Int,
        model: JournalMedia,
    ) {
        holder.bindTo(model)
    }
}