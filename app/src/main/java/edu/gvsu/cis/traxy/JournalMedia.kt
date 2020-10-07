package edu.gvsu.cis.traxy

enum class MediaType {
    NONE, TEXT, PHOTO, AUDIO, VIDEO

}

data class JournalMedia(
    val _key: String = "",
    val caption: String = "",
    val date: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    var type: Int = 0,
    var url: String = "",
    var thumbnailUrl: String = "",
)