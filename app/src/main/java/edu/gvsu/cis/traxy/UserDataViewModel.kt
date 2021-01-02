package edu.gvsu.cis.traxy

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.libraries.places.api.model.Place
import org.joda.time.DateTime

class UserDataViewModel(app: Application) : AndroidViewModel(app) {
    var userId = MutableLiveData<String>()
    val tripStart = MutableLiveData<DateTime>()
    val tripEnd = MutableLiveData<DateTime>()
    val tripPlace = MutableLiveData<Place>()
    lateinit var journals: LiveData<List<Journal>>
    val repo : TraxyRepository

    init {
        repo = TraxyRepository()
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): String? {
        val u = repo.firebaseSignInWithEmail(email, password)
        if (u != null)
            userId.postValue(u)
        journals = repo.journalLiveData
        return u
    }

    suspend fun signUpWithEmailAndPassword(email: String, password: String):String? {
        val u = repo.firebaseSignUpWithEmail(email, password)
        if (u != null)
            userId.postValue(u)
        journals = repo.journalLiveData
        return u
    }

    fun signOut() {
        repo.firebaseSignOut()
        userId.value = null
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