package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.data.local.LocalChapter
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import com.shicheeng.copymanga.resposity.MangaInfoRepository
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import com.shicheeng.copymanga.util.UIState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MangaInfoViewModel @AssistedInject constructor(
    @Assisted private val pathWord: String,
    private val repository: MangaHistoryRepository,
    private val infoRepository: MangaInfoRepository,
    private val setting: SettingPref,
) : ViewModel() {

    private val _historyFlowChapter = repository.fetchMangaChapterByPathWordFlow(pathWord)

    private val _chapter = MutableStateFlow<UIState<List<LocalChapter>>>(UIState.Loading)
    val chapters = _chapter.asStateFlow()
    private val _mangaInfo = MutableStateFlow<UIState<MangaHistoryDataModel>>(UIState.Loading)
    val mangaInfo = _mangaInfo.asStateFlow()

    private val _selectedChapter = MutableStateFlow<List<LocalChapter>>(emptyList())
    val selectChapter = _selectedChapter.asStateFlow()

    val lastWatchChapter = combine(
        flow = _chapter,
        flow2 = _mangaInfo
    ) { uiStateChapter: UIState<List<LocalChapter>>, uiStateInfo: UIState<MangaHistoryDataModel> ->
        when {
            uiStateChapter is UIState.Success && uiStateInfo is UIState.Success -> {
                if (uiStateChapter.content.isNotEmpty()){
                    uiStateChapter.content[uiStateInfo.content.positionChapter]
                }else null
            }
            else -> {
                null
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )


    init {
        onInfoLoad()
        onChapterLoad()
        viewModelScope.launch {
            repository.fetchMangaChapterByPathWordFlow(pathWord).collectLatest {
                it?.let {
                    _chapter.emit(UIState.Success(it))
                }
            }

        }
    }

    fun onInfoLoad() = viewModelScope.launch {
        _chapter.emit(UIState.Loading)
        try {
            val mangaInfoContent = infoRepository.fetchMangaInfo(pathWord)
            _mangaInfo.emit(UIState.Success(mangaInfoContent))
        } catch (e: Exception) {
            e.printStackTrace()
            _mangaInfo.emit(UIState.Error(e))
        }
    }

    fun selectItem(item: LocalChapter, isAdd: Boolean) = viewModelScope.launch {
        _selectedChapter.update {
            if (isAdd) {
                it.plus(item)
            } else {
                it.minus(item)
            }
        }
    }

    fun deselectedAllItem() {
        _selectedChapter.update {
            emptyList()
        }
    }

    fun selectFirst5(): List<LocalChapter>? {
        return if (chapters.value is UIState.Success) {
            (chapters.value as UIState.Success).content.take(5)
        } else null
    }

    fun selectLast5(): List<LocalChapter>? {
        return if (chapters.value is UIState.Success) {
            (chapters.value as UIState.Success).content.takeLast(5)
        } else null
    }

    private fun onChapterLoad() = viewModelScope.launch {
        try {
            _chapter.emit(UIState.Loading)
            val mangaChapter = infoRepository.fetchMangaChapters(pathWord)
            _chapter.emit(UIState.Success(mangaChapter))
        } catch (e: Exception) {
            e.printStackTrace()
            _chapter.emit(UIState.Error(e))
        }
    }

    fun chapterLoadForce() = viewModelScope.launch {
        try {
            _chapter.emit(UIState.Loading)
            _mangaInfo.emit(UIState.Success(infoRepository.fetchMangaInfoForce(pathWord)))
            _chapter.emit(UIState.Success(infoRepository.fetchMangaChaptersForce(pathWord)))
        } catch (e: Exception) {
            e.printStackTrace()
            _chapter.emit(UIState.Error(e))
        }
    }

    fun comicUpdate(enable: Boolean) = viewModelScope.launch {
        repository.getHistoryByMangaPathWord(pathWord)?.let {
            val newData = it.copy(isSubscribe = enable)
            repository.update(newData)
            _mangaInfo.emit(UIState.Success(newData))
        }
    }

    fun enableComicUpdate(enable: Boolean) {
        setting.enableComicsUpdateFetch(enable)
    }


    @AssistedFactory
    interface InfoViewModelFactory {
        fun create(
            pathWord: String,
        ): MangaInfoViewModel
    }

    companion object {
        fun provideAssistedViewModel(
            assistedInject: InfoViewModelFactory,
            pathWord: String,
        ) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedInject.create(pathWord) as T
            }
        }
    }

}


