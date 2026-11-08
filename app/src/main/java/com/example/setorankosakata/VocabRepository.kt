package com.example.setorankosakata

import androidx.lifecycle.LiveData

class VocabRepository(private val vocabDao: VocabDao) {

    val allVocabs: LiveData<List<Vocab>> = vocabDao.getAllVocabs()

    suspend fun insert(vocab: Vocab) {
        vocabDao.insert(vocab)
    }

    suspend fun update(vocab: Vocab) {
        vocabDao.update(vocab)
    }

    suspend fun delete(vocab: Vocab) {
        vocabDao.delete(vocab)
    }

    suspend fun getVocabById(id: Int): Vocab? {
        return vocabDao.getVocabById(id)
    }
}
