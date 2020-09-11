package edu.gvsu.cis.traxy

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class TraxyRepository(private val dao:TraxyDao) {
    private val auth = Firebase.auth

    val journalLiveData = dao.getAllJournals()

    fun firebaseSignInWithEmail(email: String, password: String): MutableLiveData<String?> {
        val uidResponse = MutableLiveData<String?>()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                auth.currentUser?.let {
                    uidResponse.value = it.uid
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

   suspend fun addJournal(d: Journal) {
        dao.insertJournal(d)
    }
}
