package edu.gvsu.cis.traxy

import com.google.firebase.firestore.DocumentId
import org.joda.time.DateTime

open class ListItem() {}

data class Journal(
    @DocumentId
    val key: String = "",
    val name: String,
    val address: String,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val placeId: String = "",
    val startDate: String,
    val endDate: String
) : ListItem()

data class Header(val title: String) : ListItem()
