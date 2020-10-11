package edu.gvsu.cis.traxy

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_journal_media_edit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class JournalMediaEditFragment : Fragment() {

    val mediaModel by activityViewModels<MediaViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_journal_media_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab_cover_photo.setOnClickListener {
            fab_cover_photo.isSelected = !fab_cover_photo.isSelected
        }

    }

    override fun onResume() {
        super.onResume()
        mediaModel.selectedMedia.observe(viewLifecycleOwner) {
            when(it.type) {
                MediaType.PHOTO.ordinal -> {
                    Glide.with(this@JournalMediaEditFragment)
                        .load(it.url)
                        .into(media_image)
                    media_image.visibility = View.VISIBLE
                }
                MediaType.VIDEO.ordinal -> {
                    Glide.with(this@JournalMediaEditFragment)
                        .load(it.thumbnailUrl)
                        .into(media_image)
                    media_image.visibility = View.VISIBLE
                }
                else -> media_image.visibility = View.GONE
            }
            media_caption.text.clear()
            media_caption.text.insert(0, it.caption)
            media_date_time.text.clear()
            media_date_time.text.insert(0, it.date)
        }
        mediaModel.selectedJournal.observe(viewLifecycleOwner) {
            media_location.text.clear()
            media_location.text.insert(0, it.address)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_edit_media, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_save_media) {
            return true
        }
        return false
    }
}