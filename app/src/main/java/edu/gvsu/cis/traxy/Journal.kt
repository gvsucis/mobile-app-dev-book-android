package edu.gvsu.cis.traxy

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId

open class ListItem() {}

@Entity
data class Journal(
    @PrimaryKey
    @DocumentId
    val key: String = "",
    val name: String = "",
    val address: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val placeId: String = "",
    val startDate: String = "",
    val endDate: String = ""
) : ListItem()

data class Header(val title: String) : ListItem()
