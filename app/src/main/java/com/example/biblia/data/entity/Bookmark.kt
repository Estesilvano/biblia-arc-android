package com.example.biblia.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "verse_id") val verseId: Int,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
