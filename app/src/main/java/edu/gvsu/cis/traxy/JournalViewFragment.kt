package edu.gvsu.cis.traxy

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_journal_view.*


class JournalViewFragment : Fragment() {

    val CAPTURE_PHOTO_REQUEST = 0xC001;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_journal_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        media_fab.addOnMenuItemClickListener { _, _, itemId ->
            when (itemId) {
                R.id.add_audio -> println("Add audio")
                R.id.add_photo_from_camera -> {
                    println("Add photo from camera")
                    val capture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    capture.resolveActivity(requireActivity().packageManager)?.let {
                        startActivityForResult(capture, CAPTURE_PHOTO_REQUEST)
                    }
                }
                R.id.add_photo_from_album -> println("Add photo from album")
                R.id.add_video -> println("Add video")
                R.id.add_text -> {
                }


            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CAPTURE_PHOTO_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    val thumbnail = data?.extras?.get("data") as Bitmap?
                    thumbnail?.let {
                        photo_view.setImageBitmap(it)
                    }
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}