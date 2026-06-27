package com.example.biblia.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey val id: Int,
    val name: String,
    val abbreviation: String,
    val testament: Int,
    @ColumnInfo(name = "book_order") val bookOrder: Int
)
