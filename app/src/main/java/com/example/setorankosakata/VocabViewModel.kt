package com.example.setorankosakata

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class VocabViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VocabRepository
    val allVocabs: LiveData<List<Vocab>>

    init {
        val dao = VocabDatabase.getDatabase(application).vocabDao()
        repository = VocabRepository(dao)
        allVocabs = repository.allVocabs
    }

    fun insert(vocab: Vocab) = viewModelScope.launch {
        repository.insert(vocab)
    }

    fun update(vocab: Vocab) = viewModelScope.launch {
        repository.update(vocab)
    }

    fun delete(vocab: Vocab) = viewModelScope.launch {
        repository.delete(vocab)
    }

    suspend fun getVocabById(id: Int): Vocab? {
        return repository.getVocabById(id)
    }
}
