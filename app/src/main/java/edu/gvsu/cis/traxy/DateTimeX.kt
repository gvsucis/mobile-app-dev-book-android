package edu.gvsu.cis.traxy

import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat

val outputFormatter = DateTimeFormat.forPattern("yyyy-MMM-dd hh:mm")

fun LocalDateTime.toPrettyDateTime(): String {
    return this.toString(outputFormatter)
}

fun DateTime.toPrettyDateTime(): String {
    return this.toString(outputFormatter)
}