package com.example.biblia.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.biblia.data.dao.BibleDao
import com.example.biblia.data.entity.Book
import com.example.biblia.data.entity.Bookmark
import com.example.biblia.data.entity.Commentary
import com.example.biblia.data.entity.Strong
import com.example.biblia.data.entity.Verse

@Database(
    entities = [Book::class, Verse::class, Strong::class, Commentary::class, Bookmark::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bibleDao(): BibleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "biblia_arc.db"
                )
                    .createFromAsset("raw/biblia_arc.db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
