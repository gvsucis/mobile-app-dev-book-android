package edu.gvsu.cis.traxy

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime
import java.util.*

open class ListItem() {}

@Entity
data class Journal(
    @PrimaryKey
    var key: String = "",
    val name: String = "", val location: String = "",
    val startDate:String = "", val endDate: String = ""
) : ListItem() {}

data class Header(val title: String) : ListItem()
