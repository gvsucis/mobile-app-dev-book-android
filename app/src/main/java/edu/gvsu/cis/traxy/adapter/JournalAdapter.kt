package edu.gvsu.cis.traxy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.gvsu.cis.traxy.JournalDiffUtil
import edu.gvsu.cis.traxy.JournalViewHolder
import edu.gvsu.cis.traxy.R
import edu.gvsu.cis.traxy.TitleViewHolder
import edu.gvsu.cis.traxy.model.Header
import edu.gvsu.cis.traxy.model.Journal
import edu.gvsu.cis.traxy.model.ListItem

class JournalAdapter(@LayoutRes val layoutId: Int, val listener: ((Journal, String) -> Unit)? = null) :
    ListAdapter<ListItem, RecyclerView.ViewHolder>(JournalDiffUtil()) {

    private val JOURNAL_TYPE = 1
    private val HEADER_TYPE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
            JOURNAL_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(layoutId, parent, false)
                return JournalViewHolder(view)

            }
            HEADER_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.journal_header_row, parent, false)
                return TitleViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            JOURNAL_TYPE -> {
                (holder as JournalViewHolder).bindTo((getItem(position) as Journal), listener)
            }
            HEADER_TYPE -> (holder as TitleViewHolder).bindTo(getItem(position) as Header)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Journal -> JOURNAL_TYPE
            is Header -> HEADER_TYPE
            else -> 0
        }
    }
}