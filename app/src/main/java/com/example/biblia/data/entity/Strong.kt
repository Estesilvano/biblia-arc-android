package com.example.biblia.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "strongs")
data class Strong(
    @PrimaryKey val number: String,
    val word: String?,
    val transliteration: String?,
    val definition: String?,
    val language: String
)
