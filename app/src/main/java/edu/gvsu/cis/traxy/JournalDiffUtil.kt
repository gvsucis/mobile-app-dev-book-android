package edu.gvsu.cis.traxy

import androidx.recyclerview.widget.DiffUtil
import edu.gvsu.cis.traxy.model.Header
import edu.gvsu.cis.traxy.model.Journal
import edu.gvsu.cis.traxy.model.ListItem

class JournalDiffUtil : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        if (oldItem is Journal && newItem is Journal)
            return oldItem.key == newItem.key
        if (oldItem is Header && newItem is Header)
            return oldItem.title == newItem.title
        return false
    }


    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        if (oldItem is Journal && newItem is Journal)
            return oldItem.key == newItem.key &&
                    oldItem.name == newItem.name &&
                    oldItem.placeId == newItem.placeId
        if (oldItem is Header && newItem is Header)
            return oldItem.title == newItem.title
        return false
    }
}