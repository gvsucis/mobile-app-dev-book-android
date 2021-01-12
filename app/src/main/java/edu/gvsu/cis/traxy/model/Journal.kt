package edu.gvsu.cis.traxy.model

import com.google.firebase.firestore.DocumentId

open class ListItem() {}

data class Journal(
    @DocumentId
    var key: String = "",
    var name: String = "",
    var startDate:String = "", var endDate: String = "",
    var address: String = "",
    var placeId: String = "",
    var lat:Double = 0.0, var lng:Double = 0.0,
    var coverPhotoUrl: String? = null
) : ListItem() {}

data class Header(val title: String) : ListItem()
