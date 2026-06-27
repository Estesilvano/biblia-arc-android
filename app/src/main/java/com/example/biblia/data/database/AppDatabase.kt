package com.example.biblia.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.biblia.data.database.dao.BibleDao
import com.example.biblia.data.database.entity.Book
import com.example.biblia.data.database.entity.Bookmark
import com.example.biblia.data.database.entity.Commentary
import com.example.biblia.data.database.entity.Strong
import com.example.biblia.data.database.entity.Verse
import java.io.File
import java.io.FileOutputStream

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
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "biblia_arc.db")
                .createFromAsset("raw/biblia_arc.db")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
