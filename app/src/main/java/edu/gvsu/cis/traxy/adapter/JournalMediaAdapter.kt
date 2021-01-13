package edu.gvsu.cis.traxy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import edu.gvsu.cis.traxy.R
import edu.gvsu.cis.traxy.model.JournalMedia

class JournalMediaAdapter(
    opt: FirestoreRecyclerOptions<JournalMedia>,
    /* the second arg is either "EDIT" or "VIEW" */
    val listener: (JournalMedia, String) -> Unit,
) :
    FirestoreRecyclerAdapter<JournalMedia, JournalMediaViewHolder>(opt) {

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
        holder.bindTo(model, listener)
    }
}