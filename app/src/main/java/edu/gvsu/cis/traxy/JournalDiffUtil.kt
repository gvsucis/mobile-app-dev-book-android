package edu.gvsu.cis.traxy

import androidx.recyclerview.widget.DiffUtil

class JournalDiffUtil : DiffUtil.ItemCallback<Journal>() {
    override fun areItemsTheSame(oldItem: Journal, newItem: Journal) =
        oldItem.key == newItem.key


    override fun areContentsTheSame(oldItem: Journal, newItem: Journal) =
        oldItem.key == newItem.key &&
                oldItem.name == newItem.name &&
                oldItem.location == newItem.location


}