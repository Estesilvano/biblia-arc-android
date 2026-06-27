package com.example.biblia.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "verses",
    foreignKeys = [ForeignKey(
        entity = Book::class,
        parentColumns = ["id"],
        childColumns = ["book_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["book_id", "chapter", "verse"], unique = true)]
)
data class Verse(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "book_id") val bookId: Int,
    val chapter: Int,
    val verse: Int,
    val text: String
)
