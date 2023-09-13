@file:Suppress("UNCHECKED_CAST")

package com.shicheeng.copymanga.ui.screen.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shicheeng.copymanga.resposity.MangaTopicDetailRepository
import com.shicheeng.copymanga.util.UIState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TopicViewModel @AssistedInject constructor(
    @Assisted private val pathWord: String,
    @Assisted private val type: Int,
    private val mangaTopicDetailRepository: MangaTopicDetailRepository,
) : ViewModel() {

    private val _pathWord = MutableStateFlow(pathWord)

    @OptIn(ExperimentalCoroutinesApi::class)
    val list = _pathWord.flatMapLatest {
        mangaTopicDetailRepository.mangas(it, type)
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = _pathWord.flatMapLatest {
        mangaTopicDetailRepository.load(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UIState.Loading
    )


    fun retry() = viewModelScope.launch {
        _pathWord.emit(pathWord)
    }

    @AssistedFactory
    interface Factory {
        fun create(pathWord: String, type: Int): TopicViewModel
    }

    companion object {
        fun inFactory(
            pathWord: String,
            type: Int,
            factory: Factory,
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return factory.create(pathWord, type) as T
            }
        }
    }

}