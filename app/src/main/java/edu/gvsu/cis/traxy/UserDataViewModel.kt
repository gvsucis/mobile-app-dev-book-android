package edu.gvsu.cis.traxy

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import kotlin.random.Random

class UserDataViewModel(app: Application) : AndroidViewModel(app) {
    var userId: String? = null
    val repo : TraxyRepository
    lateinit var remoteJournals: LiveData<List<Journal>>
    val localJournals: LiveData<List<Journal>>

    init {
        val dao = TraxyDB.getInstance(app).traxyDao()
        val dataGen = Faker()
        val today = DateTime.now()
        val rand = Random(System.currentTimeMillis())
//        CoroutineScope(Dispatchers.IO).launch {
//            dao.deleteAll()
        repo = TraxyRepository(dao)
        repeat(10) {
            val startOn = today.plusDays(rand.nextInt(-100, 100))
            val randomName = generateSequence { dataGen.lorem.words() }
                .take(7)
                .joinToString(" ")
            val d = Journal(
                "key-$it",
                randomName,
                dataGen.address.cityWithState(),
                startOn.toString(),
                startOn.plusDays(rand.nextInt(10)).toString()
            )
//                dao.insertJournal(d)
            repo.addJournal(d)
        }
//        }
        localJournals = repo.journalLocalLiveData
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): String? {
        val userId = repo.firebaseSignInWithEmail(email, password)
        remoteJournals = repo.journalCloudLiveData
        return userId
    }

    suspend fun signUpWithEmailAndPassword(email: String, password: String):String? {
        repo.firebaseSignUpWithEmail(email, password)
        remoteJournals = repo.journalCloudLiveData
        return userId
    }

    fun signOut() {
        repo.firebaseSignOut()
        userId = null
    }

    fun addJournal(z: Journal) = viewModelScope.launch(Dispatchers.IO) {
        repo.addJournal(z)
    }


    fun getJournalByKey(key: String): Journal? =
        localJournals.value?.firstOrNull {
            print("Where are we ${it.key}")
            it.key == key
        }


}