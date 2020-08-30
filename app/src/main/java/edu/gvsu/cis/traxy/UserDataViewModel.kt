package edu.gvsu.cis.traxy

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.serpro69.kfaker.Faker
import org.joda.time.DateTime
import kotlin.random.Random

class UserDataViewModel : ViewModel() {
    lateinit var userId : MutableLiveData<String?>
    var _journals = MutableLiveData<MutableList<Journal>>()
    val repo = TraxyRepository()
    init {
        val dataGen = Faker()
        val today = DateTime.now()
        val rand = Random(System.currentTimeMillis())
        repeat(100) {
            val startOn = today.plusDays(rand.nextInt(-100, 100))
            val randomName = generateSequence { dataGen.lorem.words() }
                .take(7).joinToString(" ")
            val d = Journal(
                "Key-$it",
                randomName,
                dataGen.address.cityWithState(),
                startOn, startOn.plusDays(rand.nextInt(10))
            )
            addJournal(d);
        }
    }

//    val userId
//        get() = _usr

    val journals get() = _journals

    fun signInWithEmailAndPassword(email:String, password:String) {
        userId = repo.firebaseSignInWithEmail(email, password)
    }

    fun addJournals(newData: List<Journal>) {
        // Create a new list when the current one is null
        val current = _journals.value ?: mutableListOf<Journal>()
        current.addAll(newData)
        _journals.value = current
    }

    fun addJournal(z: Journal) {
        addJournals(listOf(z))
    }

    fun getJournalByKey(key: String): Journal? =
        _journals.value?.firstOrNull {
            print("Where are we ${it.key}")
            it.key == key
        }


}