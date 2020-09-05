package edu.gvsu.cis.traxy

import androidx.lifecycle.LiveData
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class FirebaseJournalLiveData(val topRef: DatabaseReference) : LiveData<List<Journal>>() {

    override fun onActive() {
        topRef.addValueEventListener(valueListener)
    }

    override fun onInactive() {
        topRef.removeEventListener(valueListener)
    }

    val valueListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val allJournals = ArrayList<Journal>()
            snapshot.children.forEach {
                it.getValue<Journal>()?.let {
                    allJournals.add(it)
                }
            }
            value = allJournals
        }

        override fun onCancelled(error: DatabaseError) {
            topRef.removeEventListener(this)
        }

    }
}