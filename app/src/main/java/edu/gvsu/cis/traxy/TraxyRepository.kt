package edu.gvsu.cis.traxy

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import edu.gvsu.cis.traxy.model.Journal
import edu.gvsu.cis.traxy.model.JournalMedia
import edu.gvsu.cis.traxy.model.MediaType
import edu.gvsu.cis.traxy.webservice.OpenWeatherMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.joda.time.format.DateTimeFormat
import java.io.File
import java.util.*

object TraxyRepository {
    private val auth = Firebase.auth
    private val dbStore = Firebase.firestore
    private val storageRef = Firebase.storage
    private var docRef: DocumentReference? = null
    private var userMediaStore: StorageReference? = null
    private val fmt = DateTimeFormat.forPattern("YYYY-MM-dd'T'HH:mm:ssZZ")
    private val httpClient = OkHttpClient()
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
                "lng" to m.lng,
                "temperature" to m.temperature,
                "weatherIcon" to m.weatherIcon
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
            if (m.temperature != null)
                mediaData.put("temperature", m.temperature!!)
            if (m.weatherIcon != null)
                mediaData.put("weatherIcon", m.weatherIcon!!)
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
    private suspend fun getDataFromURL(url: String): String? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url).build()
        httpClient.newCall(request).execute().run {
            if (!isSuccessful)
                return@withContext null
            else
                return@withContext body()!!.string()
        }
    }

    suspend fun getWeatherData(lat: Double, lng: Double):Pair<Double,String>? {
        return try {
            val w = OpenWeatherMap.apiService.getWeatherAt(lat,lng/*, BuildConfig.OWM_API_KEY*/)
            println("Got here " + w.main)
            Pair(w.main.temp, w.weather.get(0).icon)
        } catch(e:Throwable) {
            null
        }
    }


}

// Convert from Kelvin to Fahrenheit
//fun Double.toFahrenheit(): Double = (this - 273.15) * 9/5 + 32
