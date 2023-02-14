package com.shicheeng.copymanga.viewmodel

import android.content.SharedPreferences
import androidx.annotation.AnyThread
import androidx.lifecycle.*
import com.shicheeng.copymanga.data.*
import com.shicheeng.copymanga.fm.domain.ChapterLoader
import com.shicheeng.copymanga.fm.domain.PagerLoader
import com.shicheeng.copymanga.fm.reader.ReaderMode
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import com.shicheeng.copymanga.util.FileUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class ReaderViewModel(
    private val repository: MangaHistoryRepository,
    private val sharedPreferences: SharedPreferences,
    private val pagerLoader: PagerLoader,
    private val fileUtil: FileUtil,
    private val list: Array<MangaInfoChapterDataBean>,
    private val initChapter: MangaInfoChapterDataBean,
) : ViewModel() {


    private val chapterLoader = ChapterLoader(fileUtil)
    private var loadJob: Job? = null
    private val chapters: LinkedHashMap<String, MangaInfoChapterDataBean> get() = chapterLoader.chapters

    private val _pagePosition = MutableLiveData<Int>()

    private val _hide = MutableLiveData(true)
    val hide: LiveData<Boolean> = _hide

    private val _loadingCounter = MutableLiveData(false)
    val loadingCounter: LiveData<Boolean> get() = _loadingCounter

    private val _errorHandler = MutableLiveData<Throwable>()
    val errorHandler: LiveData<Throwable> get() = _errorHandler

    val information = MutableLiveData<ReaderState>(null)
    val historyData = MutableLiveData<MangaHistoryDataModel?>(null)
    val state = MutableStateFlow(initChapter.toMangaState())
    val mangaContent = MutableLiveData(ReaderContent(emptyList(), null))
    val readerModel = MutableLiveData<ReaderMode>()
    val pagerLoaderIn = pagerLoader

    init {
        loadImp()
    }


    fun retry() {
        loadJob?.cancel()
        loadImp()
    }

    fun insertOrUpdateHistory(mangaHistoryDataModel: MangaHistoryDataModel) =
        viewModelScope.launch {
            repository.addOrUpdateHistory(
                mangaHistoryDataModel.pathWord,
                mangaHistoryDataModel,
                this
            )
        }


    private fun loadPrevNextChapter(uuid: String?, isNext: Boolean) {
        loadJob = loadJop(Dispatchers.Default) {
            chapterLoader.loadPrevNextChapter(list, uuid, isNext)
            mangaContent.postValue(ReaderContent(chapterLoader.snapshot(), null))
        }
    }

    private fun loadHistory(pathWord: String) = viewModelScope.launch {
        val historyDataModel = repository.getHistoryByMangaPathWord(pathWord)
        historyData.postValue(historyDataModel)
    }

    fun getCurrentReaderState() = state.value

    val currentChapterPage: List<MangaReaderPage>
        get() {
            val id = state.value.uuid
            return chapterLoader.getPage(id)
        }

    fun onPagePositionChange(position: Int) {
        val pages = mangaContent.value?.list ?: return
        _pagePosition.postValue(position)
        pages.getOrNull(position)?.let {
            state.update { mangaState ->
                mangaState.copy(uuid = it.uuid ?: return, page = it.index)
            }
        }
        onInfoChange()
        if (pages.isEmpty() || loadJob?.isActive == true) return
        if (position <= 2) loadPrevNextChapter(pages.first().uuid, isNext = false)
        if (position >= pages.size - 2) loadPrevNextChapter(pages.last().uuid, isNext = true)
    }

    override fun onCleared() {
        pagerLoader.close()
        super.onCleared()
    }

    @AnyThread
    private fun onInfoChange() {
        val state = getCurrentReaderState()
        val chapter = state.uuid.let(chapters::get)
        val positionChapter = list.indexOfFirst { it.uuidText == state.uuid }
        val readerState = ReaderState(
            chapterName = chapter?.chapterTitle,
            subTime = chapter?.chapterTime,
            uuid = chapter?.uuidText,
            totalPage = if (chapter == null) 0 else chapterLoader[chapter.uuidText],
            currentPage = state.page,
            chapterPosition = positionChapter
        )
        information.postValue(readerState)
    }

    fun menuHide(hide: Boolean) {
        _hide.postValue(hide)
    }

    fun saveCurrentState(nowState: MangaState? = null) {
        if (nowState != null) {
            state.value = nowState
        }
    }

    fun switchMode(readerMode: ReaderMode) = viewModelScope.launch {
        readerModel.value = readerMode
        mangaContent.value?.run {
            mangaContent.value = copy(state = getCurrentReaderState())
        }
    }

    fun switchChapter(uuid: String?) {
        if (uuid != null) {
            val prevJob = loadJob
            loadJob = loadJop(Dispatchers.Default) {
                prevJob?.cancelAndJoin()
                mangaContent.postValue(ReaderContent(emptyList(), null))
                chapterLoader.loadSingleChapter(initChapter.pathWord, uuid)
                mangaContent.postValue(ReaderContent(chapterLoader.snapshot(), MangaState(uuid, 0)))
            }
        }
    }

    private fun loadImp() {
        loadJob = loadJop(Dispatchers.Default) {
            list.forEach {
                chapters[it.uuidText] = it
            }
            loadHistory(initChapter.pathWord)
            val mode = when (sharedPreferences.getString(
                "pref_orientation_key", ReaderMode.NORMAL.name
            )) {
                ReaderMode.NORMAL.name -> ReaderMode.NORMAL
                ReaderMode.WEBTOON.name -> ReaderMode.WEBTOON
                ReaderMode.STANDARD.name -> ReaderMode.STANDARD
                else -> return@loadJop
            }
            readerModel.postValue(mode)
            chapterLoader.loadSingleChapter(initChapter.pathWord, state.value.uuid)
            onInfoChange()
            mangaContent.postValue(ReaderContent(chapterLoader.snapshot(), state.value))
        }
    }

    private fun loadJop(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit,
    ) = viewModelScope.launch(context, start) {
        _loadingCounter.postValue(true)
        try {
            block()
        } catch (e: Exception) {
            _errorHandler.postValue(e)
        } finally {
            _loadingCounter.postValue(false)
        }
    }

}


class ReaderViewModelFactory(
    private val repository: MangaHistoryRepository,
    private val sharedPreferences: SharedPreferences,
    private val pagerLoader: PagerLoader,
    private val fileUtil: FileUtil,
    private val list: Array<MangaInfoChapterDataBean>,
    private val initChapter: MangaInfoChapterDataBean,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReaderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReaderViewModel(
                repository,
                sharedPreferences,
                pagerLoader,
                fileUtil,
                list,
                initChapter,
            ) as T
        }
        throw IllegalArgumentException("UNKNOWN MODEL CLASS")
    }
}

fun MangaHistoryRepository.addOrUpdateHistory(
    pathWord: String,
    mangaHistoryDataModel: MangaHistoryDataModel,
    coroutineScope: CoroutineScope,
): Job = coroutineScope.launch {
    if (getHistoryByMangaPathWord(pathWord) != null) {
        update(mangaHistoryDataModel)
    } else {
        insert(mangaHistoryDataModel)
    }
}

