package edu.gvsu.cis.traxy

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

object TraxyRepository {
    private val auth = Firebase.auth
    private val dbStore = Firebase.firestore
    private val storageRef = Firebase.storage
    private var docRef: DocumentReference? = null
    private var userMediaStore: StorageReference? = null

    val journalCloudLiveData by lazy {
        val userId = auth.currentUser?.uid ?: "NONE"
        val coll = dbStore.collection("user/$userId/journals")
        FirebaseJournalLiveData(coll)
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
            "location" to d.location,
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

    suspend fun uploadMediaFile(mediaUri: Uri, mediaFile:File): String? {
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

    suspend fun uploadMediaContent(media:ByteArray, pathName:String): String? {
        userMediaStore?.let {
            val mediaRef =it.child(pathName)
            val mediaTask = mediaRef.putBytes(media)
            mediaTask.await()
            return mediaRef.downloadUrl.await().toString()
        }
        return null
    }

    fun addMediaEntry(key: String, m: JournalMedia) {
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
                .document(key)
                .collection("media")
                .add(mediaData)
        }
    }

    fun getMediaQuery(key:String): Query {
        val userId = auth.currentUser?.uid ?: "NONE"
        return dbStore.collection("user/$userId/journals/$key/media")
    }
}
