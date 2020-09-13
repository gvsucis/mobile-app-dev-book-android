package edu.gvsu.cis.traxy

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener

class FirebaseJournalLiveData(val topRef: CollectionReference) : LiveData<List<Journal>>() {

    lateinit var listener: ListenerRegistration
    override fun onActive() {
        listener = topRef.addSnapshotListener(docListener)
//        topRef.addSnapshotListener { s,e ->
//            if (e != null)
//                return@addSnapshotListener
//            s?.let {
//                val all = ArrayList<Journal>()
//                s.documents.forEach {
//                    it.toObject(Journal::class.java)?.let {
//                        all.add(it)
//                    }
//                }
//                postValue(all)
//            }
//        }
    }

    override fun onInactive() {
        listener.remove()
    }


    val docListener = EventListener<QuerySnapshot>() { snapShot, error ->
        if (error != null) {
            return@EventListener
        }
        snapShot?.let {
            val all = ArrayList<Journal>()
            it.documentChanges.forEach {
                Log.d("Traxy", "${it.type.name} => ${it.document.data}")
                all.add(it.document.toObject(Journal::class.java))
            }
            postValue(all)
        }
    }
}