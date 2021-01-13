package edu.gvsu.cis.traxy.viewmodel

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.firestore.Query
import edu.gvsu.cis.traxy.model.Journal
import edu.gvsu.cis.traxy.model.JournalMedia
import org.joda.time.DateTime
import java.io.ByteArrayOutputStream
import java.io.File

//class MediaViewModel(app:Application): AndroidViewModel(app) {
class MediaViewModel : ViewModel() {

    val selectedJournal= MutableLiveData<Journal>()
    val selectedMedia = MutableLiveData<JournalMedia>()
    var backupMedia: JournalMedia? = null
    val mediaUri = MutableLiveData<Uri>()
    val mediaFile = MutableLiveData<File>()
    val mediaDate = MutableLiveData<DateTime>()
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
                val thumbImg: Bitmap = metadataRetriever.getFrameAtTime(0)
                metadataRetriever.release()

                // Specify the path to store it in the cloud
                val thumbImgPath = "photos/" + it.name
                    .replace(".mp4", "-thumb.jpg")
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
        TraxyRepository.addMediaEntry(selectedJournal.value?.key ?: "NO-KEY", media)
    }

    fun mediaQuery(): Query =
        TraxyRepository.getMediaQuery(selectedJournal.value?.key ?: "NO-KEY")

    suspend fun updateJournalMedia(media: JournalMedia) {
        selectedJournal.value?.let {
            TraxyRepository.updateMediaEntry(it.key, media)
        }
    }

    fun saveMediaCopy() {
        selectedMedia.value?.let {
            backupMedia = it.copy()
        }
    }

    fun restoreMediaCopy() {
        backupMedia?.let {
            selectedMedia.value = it.copy()
        }
    }
}