package edu.gvsu.cis.traxy

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserDataViewModel : ViewModel() {
    var _usr = MutableLiveData<String>()
    var _journals = MutableLiveData<MutableList<Journal>>()

    val userId
        get() = _usr

    val journals get() = _journals

    fun addJournals(newData: List<Journal>) {
        // Create a new list when the current one is null
        val current = _journals.value ?: mutableListOf<Journal>()
        current.addAll(newData)
        _journals.value = current
    }

    fun addJoourn(z:Journal) {
        addJournals(listOf(z))
    }

}