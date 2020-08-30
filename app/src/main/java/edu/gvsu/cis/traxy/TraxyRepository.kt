package edu.gvsu.cis.traxy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class TraxyRepository {
    private val auth = FirebaseAuth.getInstance()

    fun firebaseSignInWithEmail(email: String, password:String):MutableLiveData<String?> {
        val uidResponse = MutableLiveData<String?>()
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {task ->
            if (task.isSuccessful) {
                uidResponse.value = auth.currentUser?.uid
            } else {
                uidResponse.value = null
            }
        }
        return uidResponse
    }
}