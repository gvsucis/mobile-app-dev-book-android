package edu.gvsu.cis.traxy

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.libraries.places.api.model.Place
import com.google.api.LogDescriptor
import java.io.ByteArrayOutputStream
import java.io.File

//class MediaViewModel(app:Application): AndroidViewModel(app) {
class MediaViewModel : ViewModel() {

    val journalKey = MutableLiveData<String>()
    val mediaUri = MutableLiveData<Uri>()
    val mediaFile = MutableLiveData<File>()
    val mediaDate = MutableLiveData<String>()
    val mediaLocation = MutableLiveData<Place>()
    val mediaCaption = MutableLiveData<String>()

    suspend fun addMediaEntry(media: JournalMedia, mediaUri: Uri) {
        mediaFile.value?.let {

            // When uploading videos we use the first frame as its thumbnail
            // We must upload the thumbnail before the video file is
            // DELETED by uploadMediaFile (below)
            if (it.path.endsWith(".mp4")) {
                // Get the first frame
                val metadataRetriever = MediaMetadataRetriever()
                metadataRetriever.setDataSource(it.absolutePath)
                val thumbImg:Bitmap = metadataRetriever.getFrameAtTime(0)
                metadataRetriever.release()

                // Specify the path to store it in the cloud
                val thumbImgPath = "photos/" + it.name
                    .replace(".mp4", "-thumb.jpg")
                Log.d("Hans-Traxy", "addMediaEntry: Uploading thumbnail as $thumbImgPath")
                // Convert to byte array and upload
                val byteOutputStream = ByteArrayOutputStream()
                thumbImg.compress(Bitmap.CompressFormat.JPEG,
                    100, byteOutputStream)
                TraxyRepository.uploadMediaContent(byteOutputStream.toByteArray(),
                    thumbImgPath)?.also {
                    media.thumbnailUrl = it
                }
            }
            TraxyRepository.uploadMediaFile(mediaUri, it)?.also {
                media.url = it
            }

        }
        TraxyRepository.addMediaEntry(journalKey.value ?: "NO-KEY", media)
    }
}