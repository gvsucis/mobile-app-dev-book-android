package edu.gvsu.cis.traxy

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class TraxyRepository {
    private val auth = FirebaseAuth.getInstance()
    private val dbRef = FirebaseDatabase.getInstance()
    private var topRef: DatabaseReference? = null
    private lateinit var journalLiveData : MutableLiveData<ArrayList<Journal>>

    fun firebaseSignInWithEmail(email: String, password:String,
                                journals: MutableLiveData<ArrayList<Journal>>):MutableLiveData<String?> {
        val uidResponse = MutableLiveData<String?>()
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {task ->
            if (task.isSuccessful) {
                auth.currentUser?.let {
                    uidResponse.value = it.uid
                    topRef = dbRef.getReference(it.uid)
                }
                journalLiveData = journals
                topRef?.addChildEventListener(firebaseListener)
            } else {
                uidResponse.value = null
            }
        }
        return uidResponse
    }

    fun firebaseSignUpWithEmail(email: String, password:String):MutableLiveData<String?> {
        val uidResponse = MutableLiveData<String?>()
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {task ->
            uidResponse.value = null
            if (task.isSuccessful) {
                auth.currentUser?.let {
                    uidResponse.value = it.uid
                }
            }
        }
        return uidResponse
    }


    fun firebaseSignOut() {
        topRef?.removeEventListener(firebaseListener)
        auth.signOut()
    }

    fun firebaseAddJournal(d:Journal) {
        topRef?.push()?.setValue(d)
    }

    val firebaseListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val jData = snapshot.getValue(Journal::class.java) as Journal
            val current = journalLiveData.value ?: arrayListOf<Journal>()
            current.add(jData)
            journalLiveData.value = current
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            TODO("Not yet implemented")
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }
}