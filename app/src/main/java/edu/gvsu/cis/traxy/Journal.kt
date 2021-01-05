package edu.gvsu.cis.traxy

import com.google.firebase.firestore.DocumentId

open class ListItem() {}

data class Journal(
    @DocumentId
    var key: String = "",
    val name: String = "",
    val startDate:String = "", val endDate: String = "",
    val address: String = "",
    val placeId: String = "",
    val lat:Double = 0.0, val lng:Double = 0.0
) : ListItem() {}

data class Header(val title: String) : ListItem()
