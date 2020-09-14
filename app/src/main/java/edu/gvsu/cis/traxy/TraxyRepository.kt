package edu.gvsu.cis.traxy

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class TraxyRepository(private val dao: TraxyDao) {
    private val auth = Firebase.auth
    private val dbStore = Firebase.firestore
    private var docRef: DocumentReference? = null
    val journalLocalLiveData = dao.getAllJournals()
    val journalCloudLiveData by lazy {
        val userId = auth.currentUser?.uid ?: "NONE"
        val coll = dbStore.collection("user/$userId/journals")
        FirebaseJournalLiveData(coll)
    }

    suspend fun firebaseSignInWithEmail(email: String, password: String): String? {
        try {
            val z = auth.signInWithEmailAndPassword(email, password).await()
            if (z.user != null) {
                docRef = dbStore.document("user/${z.user!!.uid}")
            }
            return z.user?.uid

        } catch (e: Exception) {
            return null
        }
    }

    suspend fun firebaseSignUpWithEmail(email: String, password: String): String? {
        val z = auth.createUserWithEmailAndPassword(email, password).await()
        if (z.user != null) {
            docRef = dbStore.collection("user").document(z.user!!.uid)
            return z.user?.uid
        } else {
            return null
        }
    }


    fun firebaseSignOut() {
        auth.signOut()
    }

    fun addJournal(d: Journal) {
//        dao.insertJournal(d)
        val jData = hashMapOf(
            "name" to d.name,
            "location" to d.location,
            "startDate" to d.startDate,
            "endDate" to d.endDate
        )
        docRef?.collection("journals")?.let {
            it.add(jData)
                .addOnSuccessListener {
                    Log.d("Traxy", "addJournal: Added")
                }.addOnFailureListener {
                    Log.d("Traxy", "addJournal: can't add")
                }
        }
    }
}
