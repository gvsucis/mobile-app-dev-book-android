package edu.gvsu.cis.traxy

import androidx.lifecycle.LiveData
import com.google.api.JwtLocationOrBuilder
import com.google.firebase.firestore.*
import java.util.ArrayList

class JournalLiveData(val topRef: CollectionReference): LiveData<List<Journal>>() {
    lateinit var register: ListenerRegistration

    val docListener = EventListener<QuerySnapshot>() { snapShot, error ->
        if (error != null)
            return@EventListener
        snapShot?.let {
            val all = ArrayList<Journal>()
            it.documentChanges.forEach {
                val journal = it.document.toObject(Journal::class.java)
                all.add(journal)
            }
            postValue(all)
        }
    }

    override fun onActive() {
        register = topRef.addSnapshotListener(docListener)
    }

    override fun onInactive() {
        register.remove()
    }
}