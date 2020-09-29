package edu.gvsu.cis.traxy

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MediaViewModel: ViewModel() {
    val repo = TraxyRepository()

    val journalKey = MutableLiveData<String>()
    val photoUri = MutableLiveData<Uri>()
    private val dbRef = Firebase.database
    private val auth = Firebase.auth
    init {

    }
    val mediaRef by lazy {
        val uid = auth.currentUser?.uid ?: "NONE"
        val key = journalKey.value ?: "NO-KEY"
        dbRef.reference.child("$uid/$key/entries")
    }

    fun addJournalEntry() {

    }
}