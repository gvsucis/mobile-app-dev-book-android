package edu.gvsu.cis.traxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import edu.gvsu.cis.traxy.model.JournalMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.joda.time.DateTime

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
        GlobalScope.launch(Dispatchers.IO) {
            TraxyRepository.getWeatherData(model.lat, model.lng, DateTime.parse(model.date))
        }
    }
}