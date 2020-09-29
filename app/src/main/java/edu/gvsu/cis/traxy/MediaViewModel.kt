package edu.gvsu.cis.traxy

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.libraries.places.api.model.Place
import java.io.File

class MediaViewModel: ViewModel() {

    val journalKey = MutableLiveData<String>()
    val photoUri = MutableLiveData<Uri>()
    val mediaFile = MutableLiveData<File>()
    val mediaDate = MutableLiveData<String>()
    val mediaLocation = MutableLiveData<Place>()
    val mediaCaption = MutableLiveData<String>()

    suspend fun addMediaEntry(media:JournalMedia, mediaUri: Uri) {
        val url = TraxyRepository.uploadMediaFile(mediaUri, mediaFile.value!!)
        if (url != null)
            media.url = url
        TraxyRepository.addMediaEntry(journalKey.value?: "NO-KEY", media)
    }
}