package com.example.biblia.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookmarks",
    foreignKeys = [ForeignKey(
        entity = Verse::class,
        parentColumns = ["id"],
        childColumns = ["verse_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["verse_id"])]
)
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "verse_id") val verseId: Long,
    val type: String,
    val color: String?,
    val note: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long
)
