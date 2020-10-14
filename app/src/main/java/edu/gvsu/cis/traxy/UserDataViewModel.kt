package edu.gvsu.cis.traxy

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.libraries.places.api.model.Place
import org.joda.time.DateTime

class UserDataViewModel(app: Application) : AndroidViewModel(app) {
    var userId: String? = null
    val tripStart = MutableLiveData<DateTime>()
    val tripEnd = MutableLiveData<DateTime>()
    val tripPlace = MutableLiveData<Place>()
    lateinit var journals: LiveData<List<Journal>>
    val repo : TraxyRepository
    val localJournals: LiveData<List<Journal>>

    init {
        val dao = TraxyDB.getInstance(app).traxyDao()
        repo = TraxyRepository(dao)
        localJournals = repo.journalLocalLiveData
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): String? {
        val userId = repo.firebaseSignInWithEmail(email, password)
        journals = repo.journalLiveData
        return userId
    }

    suspend fun signUpWithEmailAndPassword(email: String, password: String):String? {
        repo.firebaseSignUpWithEmail(email, password)
        journals = repo.journalLiveData
        return userId
    }

    fun signOut() {
        repo.firebaseSignOut()
        userId = null
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