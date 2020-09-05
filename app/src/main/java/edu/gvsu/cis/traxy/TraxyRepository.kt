package edu.gvsu.cis.traxy

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class TraxyRepository {
    private val auth = Firebase.auth
    private val dbRef = Firebase.database
    private var topRef: DatabaseReference? = null

    val journalLiveData by lazy {
        val userId = auth.currentUser?.uid ?: "NONE"
        FirebaseJournalLiveData(dbRef.getReference(userId))
    }

    fun firebaseSignInWithEmail(email: String, password: String): MutableLiveData<String?> {
        val uidResponse = MutableLiveData<String?>()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                auth.currentUser?.let {
                    uidResponse.value = it.uid
                    topRef = dbRef.getReference(it.uid)
                }
            } else {
                uidResponse.value = null
            }
        }
        return uidResponse
    }

    fun firebaseSignUpWithEmail(email: String, password: String): MutableLiveData<String?> {
        val uidResponse = MutableLiveData<String?>()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
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
        auth.signOut()
    }

    fun firebaseAddJournal(d: Journal) {
        topRef?.push()?.setValue(d)
    }

}
