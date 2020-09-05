package edu.gvsu.cis.traxy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//import io.github.serpro69.kfaker.Faker
//import org.joda.time.DateTime
//import kotlin.random.Random

class UserDataViewModel : ViewModel() {
    lateinit var userId: MutableLiveData<String?>
    lateinit var journals: FirebaseJournalLiveData
    val repo = TraxyRepository()

    fun isUserIdInitalized() = ::userId.isInitialized
    fun signInWithEmailAndPassword(email: String, password: String) {
        userId = repo.firebaseSignInWithEmail(email, password)
        journals = repo.journalLiveData
    }

    fun signUpWithEmailAndPassword(email: String, password: String) {
        userId = repo.firebaseSignUpWithEmail(email, password)
    }

    fun signOut() {
        repo.firebaseSignOut()
        userId.value = null
    }

    fun addJournals(newData: List<Journal>) {
        // Create a new list when the current one is null
//        val current = _journals.value ?: mutableListOf<Journal>()
//        current.addAll(newData)
//        _journals.value = current
    }

    fun addJournal(z: Journal) {
        repo.firebaseAddJournal(z)
    }

    fun getJournalByKey(key: String): Journal? =
        journals.value?.firstOrNull {
            print("Where are we ${it.key}")
            it.key == key
        }


}