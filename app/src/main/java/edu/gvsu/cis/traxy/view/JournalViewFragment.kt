package edu.gvsu.cis.traxy.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import edu.gvsu.cis.traxy.adapter.JournalMediaAdapter
import edu.gvsu.cis.traxy.adapter.JournalMediaViewHolder
import edu.gvsu.cis.traxy.R
import edu.gvsu.cis.traxy.model.JournalMedia
import edu.gvsu.cis.traxy.viewmodel.MediaViewModel
import kotlinx.android.synthetic.main.fragment_journal_view.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.io.File


class JournalViewFragment : Fragment() {

    val CAPTURE_PHOTO_REQUEST = 0xC001
    val CAPTURE_VIDEO_REQUEST = 0xC002
    val GALLERY_PHOTO_REQUEST = 0xC003
    val mediaModel by activityViewModels<MediaViewModel>()
    private lateinit var adapter: FirestoreRecyclerAdapter<JournalMedia, JournalMediaViewHolder>
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
                R.id.add_audio -> {
                    val audioFile = createFileName("traxyau", ".m4a")
                    mediaModel.mediaFile.value = audioFile
                    val audioUri = FileProvider.getUriForFile(requireContext(),
                        "${requireActivity().packageName}.provider", audioFile)
                    mediaModel.mediaUri.value = audioUri
                    val action = JournalViewFragmentDirections.toAudioFragment()
                    findNavController().navigate(action)
                }
                R.id.add_photo_from_camera -> {
                    val capture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    capture.resolveActivity(requireActivity().packageManager)?.let {
                        val photoFile = createFileName("traxypic", ".jpg")
                        mediaModel.mediaFile.value = photoFile
                        val photoUri = FileProvider.getUriForFile(requireContext(),
                            "${requireActivity().packageName}.provider", photoFile)
                        capture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        mediaModel.mediaUri.value = photoUri
                        startActivityForResult(capture, CAPTURE_PHOTO_REQUEST)
                    }
                }
                R.id.add_photo_from_album -> {
                    val photoGallery = Intent(Intent.ACTION_PICK)
                    photoGallery.type = "image/*"
                    startActivityForResult(Intent.createChooser(photoGallery, "Select Photo"),
                        GALLERY_PHOTO_REQUEST)
                }

                R.id.add_video -> {
                    val capture = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                    capture.resolveActivity(requireActivity().packageManager)?.let {
                        val videoFile = createFileName("traxypic", ".mp4")
                        mediaModel.mediaFile.value = videoFile
                        val videoUri = FileProvider.getUriForFile(requireContext(),
                            "${requireActivity().packageName}.provider", videoFile)
                        capture.putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
                        mediaModel.mediaUri.value = videoUri
                        startActivityForResult(capture, CAPTURE_VIDEO_REQUEST)
                    }
                }
                R.id.add_text -> {
                }
            }
        }
        mediaModel.selectedJournal.observe(this.viewLifecycleOwner, Observer {
            val option = FirestoreRecyclerOptions.Builder<JournalMedia>()
                .setLifecycleOwner(viewLifecycleOwner)
                .setQuery(mediaModel.mediaQuery(), JournalMedia::class.java)
                .build()
            adapter = JournalMediaAdapter(option) { media: JournalMedia, action: String ->
                mediaModel.selectedMedia.value = media
                val navAction = if (action == "EDIT")
                    JournalViewFragmentDirections.actionToMediaEdit()
                else
                    JournalViewFragmentDirections.actionToMediaView()
                findNavController().navigate(navAction)
            }
            media_list.adapter = adapter
            media_list.layoutManager = LinearLayoutManager(requireContext())
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CAPTURE_PHOTO_REQUEST, CAPTURE_VIDEO_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    findNavController().navigate(R.id.action_to_mediaDetails)
                }
            }
            GALLERY_PHOTO_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    data?.clipData?.let {
                        val photoUri = it.getItemAt(0).uri
                        mediaModel.mediaUri.value = photoUri
                        mediaModel.mediaFile.value = File(photoUri.path!!)
                        findNavController().navigate(R.id.action_to_mediaDetails)
                    }
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun createFileName(prefix: String, ext: String): File {
        val now = DateTime.now()
        val fmt = DateTimeFormat.forPattern("yyyyMMdd-HHmmss")
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val media = File.createTempFile("${prefix}-" + fmt.print(now),
            ext, storageDir)
        return media
    }
}