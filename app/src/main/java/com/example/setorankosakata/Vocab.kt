package com.example.setorankosakata

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocab_table")
data class Vocab(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val word: String,
    val meaning: String,
    val language: String,
    val example: String,
    val date: String,
    val memorized: Boolean = false
)
