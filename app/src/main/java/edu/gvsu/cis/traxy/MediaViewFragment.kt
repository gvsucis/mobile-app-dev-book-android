package edu.gvsu.cis.traxy

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.EventListener
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.fragment_media_view.*

class MediaViewFragment : Fragment() {
    private val mediaModel by activityViewModels<MediaViewModel>()
    private var incomingOrientation = 0
    lateinit var exoPlayer: SimpleExoPlayer
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_media_view, container, false)
    }

    object exoHandler : EventListener {
        override fun onLoadingChanged(isLoading: Boolean) {
            if (!isLoading) {

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exoPlayer = SimpleExoPlayer.Builder(requireContext()).build()
        exoPlayer.addListener(exoHandler)
        videoView.player = exoPlayer
    }

    override fun onResume() {
        super.onResume()
        incomingOrientation = requireActivity().requestedOrientation

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        mediaModel.selectedMedia.observe(viewLifecycleOwner) {
            when (it.type) {
                MediaType.PHOTO.ordinal ->
                    Glide.with(this)
                        .load(it.url)
                        .into(photoView)
                MediaType.VIDEO.ordinal -> {
                    val mediaContent = MediaItem.fromUri(it.url)
                    exoPlayer.setMediaItem(mediaContent)
                    exoPlayer.prepare()
                    exoPlayer.playWhenReady = true
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        requireActivity().requestedOrientation = incomingOrientation
    }

    override fun onStop() {
        super.onStop()
        videoView.player = null
        exoPlayer.release()
    }
}