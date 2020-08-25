package edu.gvsu.cis.traxy

import org.joda.time.DateTime

data class Journal(val key: String, val name:String, val location: String,
val startDate: DateTime, val endDate: DateTime)
