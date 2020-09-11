package edu.gvsu.cis.traxy

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import kotlin.random.Random

class UserDataViewModel(app: Application) : AndroidViewModel(app) {
    lateinit var userId: MutableLiveData<String?>
    val repo : TraxyRepository

    val journals: LiveData<List<Journal>>
    init {
        val dao = TraxyDB.getInstance(app).traxyDao()
        val dataGen = Faker()
        val today = DateTime.now()
        val rand = Random(System.currentTimeMillis())
        CoroutineScope(Dispatchers.IO).launch {
            dao.deleteAll()
            repeat(10)  {
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
                dao.insertJournal(d)
            }
        }
        repo = TraxyRepository(dao)
        journals = repo.journalLiveData
    }
    fun isUserIdInitalized() = ::userId.isInitialized
    fun signInWithEmailAndPassword(email: String, password: String) {
        userId = repo.firebaseSignInWithEmail(email, password)
    }

    fun signUpWithEmailAndPassword(email: String, password: String) {
        userId = repo.firebaseSignUpWithEmail(email, password)
    }

    fun signOut() {
        repo.firebaseSignOut()
        userId.value = null
    }

    fun addJournal(z:Journal) = viewModelScope.launch(Dispatchers.IO) {
        repo.addJournal(z)
    }



    fun getJournalByKey(key: String): Journal? =
        journals.value?.firstOrNull {
            print("Where are we ${it.key}")
            it.key == key
        }


}