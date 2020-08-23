package edu.gvsu.cis.traxy

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserDataViewModel : ViewModel() {
    var _usr = MutableLiveData<String>()

    val userId
        get() = _usr

}