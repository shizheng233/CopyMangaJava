package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.shicheeng.copymanga.resposity.AuthorsMangaRepository
import com.shicheeng.copymanga.resposity.logD
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class AuthorMangaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    authorsMangaRepository: AuthorsMangaRepository,
) : ViewModel() {
    private val authorPathWordNow: String? = savedStateHandle["author_path_word"]
    private val _authorPathWord = MutableStateFlow(authorPathWordNow)

    @OptIn(ExperimentalCoroutinesApi::class)
    val list = _authorPathWord
        .filter {
            !it.isNullOrBlank() && it.isNotEmpty()
        }.filterNotNull()
        .flatMapLatest {
            it.logD()
            authorsMangaRepository.fetchMangaByPathWord(it)
        }

}