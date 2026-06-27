package com.example.biblia.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "commentaries")
data class Commentary(
    @PrimaryKey val id: Int = 0,
    @ColumnInfo(name = "verse_id") val verseId: Int,
    val title: String?,
    val text: String
)
