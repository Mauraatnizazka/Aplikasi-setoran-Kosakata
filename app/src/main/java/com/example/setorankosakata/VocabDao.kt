package com.example.setorankosakata

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface VocabDao {

    @Insert
    suspend fun insert(vocab: Vocab)

    @Update
    suspend fun update(vocab: Vocab)

    @Delete
    suspend fun delete(vocab: Vocab)

    @Query("SELECT * FROM vocab_table ORDER BY id DESC")
    fun getAllVocabs(): LiveData<List<Vocab>>

    @Query("SELECT * FROM vocab_table WHERE id = :id LIMIT 1")
    suspend fun getVocabById(id: Int): Vocab?
}
