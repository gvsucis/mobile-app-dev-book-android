package edu.gvsu.cis.traxy

import android.net.Uri
import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import edu.gvsu.cis.traxy.model.Journal
import edu.gvsu.cis.traxy.model.JournalMedia
import edu.gvsu.cis.traxy.model.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.geojson.Feature
import org.geojson.FeatureCollection
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object TraxyRepository {
    private val auth = Firebase.auth
    private val dbStore = Firebase.firestore
    private val storageRef = Firebase.storage
    private var docRef: DocumentReference? = null
    private var userMediaStore: StorageReference? = null
    private val fmt = DateTimeFormat.forPattern("YYYY-MM-dd'T'HH:mm:ssZZ")
    val journalCloudLiveData by lazy {
        val userId = auth.currentUser?.uid ?: "NONE"
        val coll = dbStore.collection("user/$userId/journals")
        JournalLiveData(coll)
    }

    suspend fun firebaseSignInWithEmail(email: String, password: String): String? {
        try {
            val z = auth.signInWithEmailAndPassword(email, password).await()
            if (z.user != null) {
                docRef = dbStore.document("user/${z.user!!.uid}")
                userMediaStore = storageRef.getReference(z.user!!.uid)
            }
            return z.user?.uid

        } catch (e: Exception) {
            return null
        }
    }

    suspend fun firebaseSignUpWithEmail(email: String, password: String): String? {
        val z = auth.createUserWithEmailAndPassword(email, password).await()
        if (z.user != null) {
            docRef = dbStore.collection("user").document(z.user!!.uid)
            userMediaStore = storageRef.getReference(z.user!!.uid)
            return z.user?.uid
        } else {
            return null
        }
    }


    fun firebaseSignOut() {
        auth.signOut()
    }

    fun addJournal(d: Journal) {
//        dao.insertJournal(d)
        val jData = hashMapOf(
            "name" to d.name,
            "placeId" to d.placeId,
            "lat" to d.lat,
            "lng" to d.lng,
            "address" to d.address,
            "startDate" to d.startDate,
            "endDate" to d.endDate
        )
        docRef?.collection("journals")?.let {
            it.add(jData)
                .addOnSuccessListener {
                    Log.d("Traxy", "addJournal: Added")
                }.addOnFailureListener {
                    Log.d("Traxy", "addJournal: can't add")
                }
        }
    }

    suspend fun uploadMediaFile(mediaUri: Uri, mediaFile: File): String? {
        userMediaStore?.let {
            val fileName = mediaUri.lastPathSegment!!
            val dirName = if (fileName.endsWith(".mp4")) "videos/" else "photos/"
            val mediaRef = it.child(dirName).child(fileName)
            Log.d("Hans-Traxy", "uploadMediaFile: Uploading media as ${mediaRef.path}")
            val mediaTask = mediaRef.putFile(mediaUri)
            mediaTask.await()
            mediaFile.delete()
            return mediaRef.downloadUrl.await().toString()
        }
        return null
    }

    suspend fun uploadMediaContent(media: ByteArray, pathName: String): String? {
        userMediaStore?.let {
            val mediaRef = it.child(pathName)
            val mediaTask = mediaRef.putBytes(media)
            mediaTask.await()
            return mediaRef.downloadUrl.await().toString()
        }
        return null
    }

    fun addMediaEntry(journalKey: String, m: JournalMedia) {
        docRef?.let {
            var mediaData = hashMapOf(
                "type" to m.type,
                "caption" to m.caption,
                "date" to m.date,
                "url" to m.url,
                "lat" to m.lat,
                "lng" to m.lng
            )
            if (m.type == MediaType.VIDEO.ordinal)
                mediaData["thumbnailUrl"] = m.thumbnailUrl
            it
                .collection("journals")
                .document(journalKey)
                .collection("media")
                .add(mediaData)
        }
    }

    fun getMediaQuery(key: String): Query {
        val userId = auth.currentUser?.uid ?: "NONE"
        return dbStore.collection("user/$userId/journals/$key/media")
    }

    suspend fun updateMediaEntry(journalKey: String, m: JournalMedia): Void? {
        return docRef?.let {
            var mediaData = hashMapOf(
                "type" to m.type,
                "caption" to m.caption,
                "date" to m.date,
                "url" to m.url,
                "lat" to m.lat,
                "lng" to m.lng
            )
            if (m.type == MediaType.VIDEO.ordinal)
                mediaData["thumbnailUrl"] = m.thumbnailUrl
            it
                .collection("journals/$journalKey/media")
                .document(m._key)
                .set(mediaData)
                .await()

        }
    }

    // https://api.weather.gov/points/45,-85
    // https://api.weather.gov/stations/KILN/observations?start=2020-10-14T03:21:26-00:00&limit=3
    private suspend fun getFromURL(url: String): String? = withContext(Dispatchers.IO) {
        val url = URL(url)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.connect()
        if (conn.responseCode == HttpURLConnection.HTTP_OK) {
            val scanner = Scanner(conn.inputStream)
            val jsonString = StringBuilder()
            while (scanner.hasNextLine())
                jsonString.append(scanner.nextLine())
            println("Got it!")
            return@withContext jsonString.toString()
        } else {
            println("Open Weather Map Error ${conn.responseCode} ${conn.responseMessage}")
            return@withContext null
        }

    }

    suspend fun getWeatherData(lat: Double, lng: Double, atTime: DateTime) {
//        val parser = Json { ignoreUnknownKeys = true }
        val s1 = getFromURL("https://api.weather.gov/points/$lat,$lng")
        s1?.run {
            val t1 = ObjectMapper().readValue(this, Feature::class.java)
            val station = t1.properties.get("radarStation") ?: "None"
            val timestamp = fmt.print(atTime).replace("+00:00","-00:00")
            getFromURL("https://api.weather.gov/stations/$station/observations?" +
                    "start=$timestamp&limit=2")
//            println("Observation is $z")

        }?.run {
            val t2 = ObjectMapper().readValue(this, FeatureCollection::class.java)
            val z = t2.features[0].properties.getValue("temperature")
            println("Temperatur is $z")
        }
        println("Here???")
    }
}