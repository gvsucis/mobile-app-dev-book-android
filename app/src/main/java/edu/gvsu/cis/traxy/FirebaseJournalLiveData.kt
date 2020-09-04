package edu.gvsu.cis.traxy

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.database.*

class FirebaseJournalLiveData(val topRef:DatabaseReference): LiveData<List<Journal>>() {

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
                val jData = it.getValue(Journal::class.java) as Journal
                allJournals.add(jData)
            }
            value = allJournals
        }

        override fun onCancelled(error: DatabaseError) {
            topRef.removeEventListener(this)
        }

    }
}