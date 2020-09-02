package edu.gvsu.cis.traxy

import org.joda.time.DateTime
import java.util.*

open class ListItem() {}

data class Journal(
    val key: String = "", val name: String = "", val location: String = "",
    val startDate:String = "", val endDate: String = ""
) : ListItem() {}

data class Header(val title: String) : ListItem()
