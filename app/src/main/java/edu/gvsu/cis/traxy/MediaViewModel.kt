package edu.gvsu.cis.traxy

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.firestore.Query
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

//class MediaViewModel(app:Application): AndroidViewModel(app) {
class MediaViewModel : ViewModel() {

//    val journalKey = MutableLiveData<String>()
    val selectedJournal= MutableLiveData<Journal>()
    val selectedMedia = MutableLiveData<JournalMedia>()
    //    val mediaKey = MutableLiveData<String>()
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

//    fun getJournalByKey(key:String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            TraxyRepository.getJournalData(key).also {
//                selectedJournal.postValue(it)
//            }
//        }
//    }
//    fun getJournalMediaByKey(key: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            TraxyRepository.getMediaEntry(selectedJournal.value?.key ?: "NO-KEY", key)?.also {
//                selectedMedia.postValue (it)
//            }
//        }
//    }

}