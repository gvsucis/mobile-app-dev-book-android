package edu.gvsu.cis.traxy

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
    val journalLiveData by lazy {
        val userId = auth.currentUser?.uid ?: "NONE"
        val coll = dbStore.collection("user/$userId/journals")
        JournalLiveData(coll)

    }

    suspend fun firebaseSignInWithEmail(email: String, password: String): String? {
        try {
            val z = auth.signInWithEmailAndPassword(email, password).await()
            return z.user?.uid.also {
                docRef = dbStore.collection("user").document(it ?: "NONE")
            }
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun firebaseSignUpWithEmail(email: String, password: String): String? {
        try {
            val z = auth.createUserWithEmailAndPassword(email, password).await()
            return z.user?.uid.also {
                docRef = dbStore.collection("user").document(it ?: "NONE")

            }
        } catch (e: java.lang.Exception) {
            return null
        }

    }


    fun firebaseSignOut() {
        auth.signOut()
    }

    fun firebaseAddJournal(d: Journal) {
//        dao.insertJournal(d)
        val jData = hashMapOf(
            "name" to d.name, "address" to d.address,
            "placeId" to d.placeId, "lat" to d.lat, "lng" to d.lng,
            "startDate" to d.startDate, "endDate" to d.endDate
        )

        docRef?.collection("journals")?.let {
            it.add(jData)
        }
    }
}
