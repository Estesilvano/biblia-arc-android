package com.example.biblia.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "commentaries",
    foreignKeys = [ForeignKey(
        entity = Verse::class,
        parentColumns = ["id"],
        childColumns = ["verse_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["verse_id"])]
)
data class Commentary(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "verse_id") val verseId: Long,
    val title: String?,
    val text: String,
    val type: String
)
