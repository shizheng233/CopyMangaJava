package com.shicheeng.copymanga.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.shicheeng.copymanga.data.*
import com.shicheeng.copymanga.fm.delegate.InfoDataDelegate
import com.shicheeng.copymanga.json.MangaInfoJson
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import com.shicheeng.copymanga.util.FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MangaInfoViewModel(
    private val pathWord: String,
    private val repository: MangaHistoryRepository,
    fileUtil: FileUtil,
) : ViewModel() {

    sealed class UiState {
        data class Success(val data: MangaInfo) : UiState()
        object Loading : UiState()
        data class Error(val error: Exception) : UiState()
    }


    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _chapterHistory = MutableStateFlow<MangaHistoryDataModel?>(null)
    private val chapterHistory = _chapterHistory.asStateFlow()
    private val _mangaChapters = MutableStateFlow<JsonObject?>(null)
    private val mangaChapters = _mangaChapters.asStateFlow()

    private val delegate = InfoDataDelegate(pathWord, fileUtil)

    val chaptersModel = combine(chapterHistory, mangaChapters) { history, chapters ->
        Log.i("TAG-CHAPTER", ": $chapterHistory")
        delegate.mapChapters(history, chapters ?: JsonObject())
    }.asLiveData(viewModelScope.coroutineContext)

    init {
        onDataLoad()
        onHistoryWanna()
    }

    fun onDataLoad() = viewModelScope.launch {

        _uiState.emit(UiState.Loading)
        try {
            withContext(Dispatchers.Default) {
                val mangaInfoContent = MangaInfoJson.getMangaInfo(pathWord)
                val mangaChapterContent = MangaInfoJson.getMangaContent(pathWord)
                Log.i("TAG.LOAD LIST", "onDataLoad: $mangaChapterContent")
                val mangaHistory = repository.getHistoryByMangaPathWord(pathWord)
                _mangaChapters.emit(mangaChapterContent)
                _chapterHistory.emit(mangaHistory)
                _uiState.emit(
                    UiState.Success(
                        MangaInfo(
                            mangaHistory,
                            mangaChapterContent,
                            mangaInfoContent
                        )
                    )
                )
            }
        } catch (e: Exception) {
            _uiState.emit(UiState.Error(e))
        }

    }

    fun onHistoryWanna() = viewModelScope.launch {
        try {
            withContext(Dispatchers.IO) {
                val mangaHistory = repository.getHistoryByMangaPathWord(pathWord)
                _chapterHistory.emit(mangaHistory)
            }
        } catch (_: Exception) {

        }
    }

}

class MangaInfoViewModelFactory(
    private val pathWord: String,
    private val repository: MangaHistoryRepository,
    private val fileUtil: FileUtil,
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MangaInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MangaInfoViewModel(pathWord, repository, fileUtil) as T
        }
        throw IllegalArgumentException("ERROR VIEW MODEL CLASS")
    }

}

data class MangaInfo(
    val mangaHistory: MangaHistoryDataModel?,
    val mangaChapterContent: JsonObject?,
    val info: MangaInfoData,
)

