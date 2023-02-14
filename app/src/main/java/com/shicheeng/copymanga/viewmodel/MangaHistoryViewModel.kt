package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shicheeng.copymanga.resposity.MangaHistoryRepository

class MangaHistoryViewModel(repository: MangaHistoryRepository) : ViewModel() {

    val history = repository.allHistoryDao

}

class MangaHistoryViewModelFactory(private val repository: MangaHistoryRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MangaHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MangaHistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("UNKNOWN CLASS NAME!")
    }

}