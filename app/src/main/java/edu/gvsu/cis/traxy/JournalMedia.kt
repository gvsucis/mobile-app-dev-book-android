package edu.gvsu.cis.traxy

enum class MediaType {
    NONE, TEXT, PHOTO, AUDIO, VIDEO
}
data class JournalMedia(
    var _key: String = "",
    var caption: String = "",
    var date: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var type: MediaType = MediaType.NONE,
    var url: String = "",
    var thumbnailUrl: String = "",
)