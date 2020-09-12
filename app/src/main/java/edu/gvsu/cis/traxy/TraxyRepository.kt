package edu.gvsu.cis.traxy

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.lang.Exception


class TraxyRepository(private val dao:TraxyDao) {
    private val auth = Firebase.auth

    val journalLiveData = dao.getAllJournals()

    suspend fun firebaseSignInWithEmail(email: String, password: String): String? {
        try {
            val z = auth.signInWithEmailAndPassword(email, password).await()
            return z.user?.uid

        } catch (e: Exception) {
            return null
        }
    }

    suspend fun firebaseSignUpWithEmail(email: String, password: String): String? {
        val z = auth.createUserWithEmailAndPassword(email, password).await()
        return z.user?.uid
    }


    fun firebaseSignOut() {
        auth.signOut()
    }

   suspend fun addJournal(d: Journal) {
        dao.insertJournal(d)
    }
}
