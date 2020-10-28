package edu.gvsu.cis.traxy.model

import com.google.firebase.firestore.DocumentId

enum class MediaType {
    NONE, TEXT, PHOTO, AUDIO, VIDEO
}

data class JournalMedia(
    @DocumentId
    val _key: String = "",

    var caption: String = "",
    var date: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var type: Int = 0,
    var url: String = "",
    var thumbnailUrl: String = "",
)