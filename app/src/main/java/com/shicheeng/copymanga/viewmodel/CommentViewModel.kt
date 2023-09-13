package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shicheeng.copymanga.resposity.ComicCommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ComicCommentRepository,
) : ViewModel() {

    private val uuid: String? = savedStateHandle["uuid_comic"]
    private val _comicUUID = MutableStateFlow(uuid)

    @OptIn(ExperimentalCoroutinesApi::class)
    val comments = _comicUUID
        .filter { !it.isNullOrBlank() && it.isNotEmpty() }
        .filterNotNull()
        .flatMapLatest {
            repository.loadComment(it)
        }.cachedIn(viewModelScope)

}