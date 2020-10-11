package edu.gvsu.cis.traxy

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId
import org.joda.time.DateTime
import java.util.*

open class ListItem() {}

@Entity
data class Journal(
    @PrimaryKey
    @DocumentId
    var key: String = "",
    val name: String = "",
    val startDate:String = "", val endDate: String = "",
    val address: String = "",
    val placeId: String = "",
    val lat:Double = 0.0, val lng:Double = 0.0
) : ListItem() {}

data class Header(val title: String) : ListItem()
