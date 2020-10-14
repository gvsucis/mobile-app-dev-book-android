package edu.gvsu.cis.traxy

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TraxyRepository {
    private val auth = FirebaseAuth.getInstance()
    private val dbStore = Firebase.firestore
    private var docRef: DocumentReference? = null

    val journalLiveData by lazy {
        val userId = auth.currentUser?.uid ?: "NONE"
        val coll = dbStore.collection("user/$userId/journals")
        JournalLiveData(coll)
    }

    fun firebaseSignInWithEmail(email: String, password:String):MutableLiveData<String?> {
        val uidResponse = MutableLiveData<String?>()
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {task ->
            if (task.isSuccessful) {
                auth.currentUser?.let {
                    uidResponse.value = it.uid
                    docRef = dbStore.collection("user").document(it.uid)
                }
            } else {
                uidResponse.value = null
            }
        }
        return uidResponse
    }

    fun firebaseSignUpWithEmail(email: String, password:String):MutableLiveData<String?> {
        val uidResponse = MutableLiveData<String?>()
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {task ->
            if (task.isSuccessful) {
                uidResponse.value = auth.currentUser?.uid
            } else {
                uidResponse.value = null
            }
        }
        return uidResponse
    }

    fun firebaseSignOut() {
        auth.signOut()
    }

    fun firebaseAddJournal(d:Journal) {
        val jData = hashMapOf("name" to d.name, "address" to d.address,
        "placeId" to d.placeId, "lat" to d.lat, "lng" to d.lng,
        "startDate" to d.startDate, "endDate" to d.endDate)

        docRef?.collection("journals")?.let {
            it.add(jData)
        }
    }

    val docListener = EventListener<QuerySnapshot>() { snapShot, error ->
        if (error != null)
            return@EventListener
        snapShot?.let {
            val all = ArrayList<Journal>()
            it.documentChanges.forEach {
                val journal = it.document.toObject(Journal::class.java)
                all.add(journal)
            }
        }

    }
}