package edu.gvsu.cis.traxy

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import edu.gvsu.cis.traxy.model.Journal
import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import kotlin.random.Random

class UserDataViewModel(app: Application) : AndroidViewModel(app) {
    var userId: String? = null

    lateinit var remoteJournals: LiveData<List<Journal>>

    init {
        val dataGen = Faker()
        val today = DateTime.now()
        val rand = Random(System.currentTimeMillis())
//        CoroutineScope(Dispatchers.IO).launch {
//            dao.deleteAll()
        repeat(10) {
            val startOn = today.plusDays(rand.nextInt(-100, 100))
            val randomName = generateSequence { dataGen.lorem.words() }
                .take(7)
                .joinToString(" ")
            val d = Journal(
                key = "key-$it",
                name = randomName,
                address = dataGen.address.cityWithState(),
                startDate = startOn.toString(),
                endDate = startOn.plusDays(rand.nextInt(10)).toString()
            )
//                dao.insertJournal(d)
            TraxyRepository.addJournal(d)
        }
//        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): String? {
        val userId = TraxyRepository.firebaseSignInWithEmail(email, password)
        remoteJournals = TraxyRepository.journalCloudLiveData
        return userId
    }

    suspend fun signUpWithEmailAndPassword(email: String, password: String):String? {
        TraxyRepository.firebaseSignUpWithEmail(email, password)
        remoteJournals = TraxyRepository.journalCloudLiveData
        return userId
    }

    fun signOut() {
        TraxyRepository.firebaseSignOut()
        userId = null
    }

    fun addJournal(z: Journal) = viewModelScope.launch(Dispatchers.IO) {
        TraxyRepository.addJournal(z)
    }

    fun updateJournal(z: Journal) = viewModelScope.launch(Dispatchers.IO) {
        TraxyRepository.updateJournal(z)
    }

    fun getJournalByKey(key: String): Journal? =
        remoteJournals.value?.firstOrNull {
            print("Where are we ${it.key}")
            it.key == key
        }


}