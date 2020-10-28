package edu.gvsu.cis.traxy

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import edu.gvsu.cis.traxy.model.Journal

@Dao
interface TraxyDao {

    @Query("SELECT * FROM Journal")
    fun getAllJournals(): LiveData<List<Journal>>

    @Insert
    suspend fun insertJournal(j: Journal)

    @Query("DELETE FROM Journal")
    suspend fun deleteAll()
}