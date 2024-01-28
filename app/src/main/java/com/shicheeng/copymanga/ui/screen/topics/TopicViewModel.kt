package com.shicheeng.copymanga.ui.screen.topics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shicheeng.copymanga.resposity.MangaTopicDetailRepository
import com.shicheeng.copymanga.util.RetryTrigger
import com.shicheeng.copymanga.util.UIState
import com.shicheeng.copymanga.util.retryableFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class TopicViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mangaTopicDetailRepository: MangaTopicDetailRepository,
) : ViewModel() {

    private val _pathWord: MutableStateFlow<String?> = MutableStateFlow(savedStateHandle["pathWord"])
    private val type: Int = savedStateHandle["type"] ?: 1
    private val _retryTiger = RetryTrigger()

    @OptIn(ExperimentalCoroutinesApi::class)
    val list = _pathWord
        .filterNotNull()
        .flatMapLatest {
            mangaTopicDetailRepository.mangas(it, type)
        }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = retryableFlow(_retryTiger) {
        _pathWord
            .filterNotNull()
            .flatMapLatest {
                mangaTopicDetailRepository.load(it)
            }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UIState.Loading
        )


    fun retry() = _retryTiger.retry()

}

