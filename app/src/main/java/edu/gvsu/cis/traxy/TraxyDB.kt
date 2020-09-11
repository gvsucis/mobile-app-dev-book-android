package edu.gvsu.cis.traxy

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.joda.time.DateTime


@Database(entities = arrayOf(Journal::class), version = 1)
public abstract class TraxyDB: RoomDatabase() {
    abstract fun traxyDao(): TraxyDao

    companion object {

        @Volatile
        private var INSTANCE: TraxyDB? = null


        fun getInstance(context: Context/*, scope: CoroutineScope*/): TraxyDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                    TraxyDB::class.java, "traxy_database")
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}