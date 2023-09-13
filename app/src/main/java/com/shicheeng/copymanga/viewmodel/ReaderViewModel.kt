package com.shicheeng.copymanga.viewmodel

import androidx.annotation.WorkerThread
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.data.MangaReaderPage
import com.shicheeng.copymanga.data.MangaState
import com.shicheeng.copymanga.data.ReaderContent
import com.shicheeng.copymanga.data.ReaderState
import com.shicheeng.copymanga.data.local.LocalChapter
import com.shicheeng.copymanga.data.local.toMangaState
import com.shicheeng.copymanga.fm.domain.ChapterLoader
import com.shicheeng.copymanga.fm.domain.PagerLoader
import com.shicheeng.copymanga.fm.reader.MangaLoader
import com.shicheeng.copymanga.fm.reader.ReaderMode
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import com.shicheeng.copymanga.resposity.MangaInfoRepository
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.collections.set
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

private const val MAX_LOAD_PAGER = 4

/**
 * 重新设计ReaderViewModel
 */
@HiltViewModel
class ReaderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MangaHistoryRepository,
    private val pagerLoader: PagerLoader,
    private val settingPref: SettingPref,
    private val mangaInfoRepository: MangaInfoRepository,
    private val chapterLoader: ChapterLoader,
) : ViewModel() {

    private val mangaLoader = MangaLoader(savedStateHandle)
    private val currentPathWord = mangaLoader.mangaPathWord
    private val currentChapterUUID = mangaLoader.mangaChapterUUID

    private val list by lazy {
        runBlocking {
            mangaInfoRepository.fetchMangaChapters(
                pathWord = requireNotNull(mangaLoader.mangaPathWord)
            )
        }
    }

    private val initChapter = list.find { x -> x.uuid == currentChapterUUID } ?: list[0]
    private var loadJob: Job? = null
    private val chapters: LinkedHashMap<String, LocalChapter> get() = chapterLoader.chapters

    private val _loadingCounter = MutableStateFlow(false)
    val loadingCounter get() = _loadingCounter.asStateFlow()

    private val _errorHandler = MutableStateFlow<Throwable?>(null)
    val errorHandler get() = _errorHandler.asStateFlow()

    val information = MutableStateFlow<ReaderState?>(null)
    private val historyData = MutableStateFlow<MangaHistoryDataModel?>(null)
    val state = MutableStateFlow(initChapter.toMangaState())
    val mangaContent = MutableStateFlow(ReaderContent(emptyList(), null))
    val readerModel = MutableStateFlow<ReaderMode?>(null)

    init {
        loadImp()
    }


    fun retry() {
        loadJob?.cancel()
        loadImp()
    }

    private fun loadPrevNextChapter(uuid: String?, isNext: Boolean) {
        loadJob = loadJop(Dispatchers.Default) {
            chapterLoader.loadPrevNextChapter(list, uuid, isNext)
            mangaContent.value = ReaderContent(chapterLoader.snapshot(), null)
        }
    }

    private fun loadHistory(pathWord: String) = viewModelScope.launch {
        val historyDataModel = repository.getHistoryByMangaPathWord(pathWord)
        historyData.emit(historyDataModel)
    }

    fun getCurrentReaderState() = state.value

    val currentChapterPage: List<MangaReaderPage>
        get() {
            val id = state.value.uuid
            return chapterLoader.getPage(id)
        }

    fun onPagePositionChange(position: Int) {
        val pages = mangaContent.value.list
        pages.getOrNull(position)?.let {
            state.update { mangaState ->
                mangaState.copy(uuid = it.uuid ?: return, page = it.index)
            }
        }
        onInfoChange()
        if (pages.isEmpty() || loadJob?.isActive == true) {
            return
        }
        if (position <= MAX_LOAD_PAGER) {
            loadPrevNextChapter(pages.first().uuid, isNext = false)
        }
        if (position >= pages.size - MAX_LOAD_PAGER) {
            loadPrevNextChapter(pages.last().uuid, isNext = true)
        }
    }


    @WorkerThread
    private fun onInfoChange() {
        val state = getCurrentReaderState()
        val chapter = state.uuid.let(chapters::get)
        val positionChapter = list.indexOfFirst { it.uuid == state.uuid }
        val readerState = ReaderState(
            chapterName = chapter?.name,
            subTime = chapter?.datetime_created,
            uuid = chapter?.uuid,
            totalPage = if (chapter == null) 0 else chapterLoader[chapter.uuid],
            currentPage = state.page,
            chapterPosition = positionChapter,
            mangaName = historyData.value?.name
        )
        information.value = readerState
        viewModelScope.launch {
            val newHistoryData = historyData.value
                ?.copy(
                    positionPage = state.page,
                    positionChapter = positionChapter,
                    time = System.currentTimeMillis()
                )
            if (newHistoryData != null) {
                repository.updateAsync(newHistoryData)
            }
        }
    }

    fun saveCurrentState(nowState: MangaState? = null) {
        if (nowState != null) {
            state.value = nowState
        }
    }


    fun switchMode(readerMode: ReaderMode) = viewModelScope.launch {
        readerModel.value = readerMode
        mangaContent.value.run {
            mangaContent.value = copy(state = getCurrentReaderState())
        }
        val renew = historyData.value?.copy(readerModeId = readerMode.id) ?: return@launch
        repository.update(renew)
        historyData.emit(renew)
    }

    fun switchChapter(uuid: String?) {
        if (uuid != null) {
            val prevJob = loadJob
            loadJob = loadJop(Dispatchers.Default) {
                prevJob?.cancelAndJoin()
                mangaContent.value = ReaderContent(emptyList(), null)
                chapterLoader.loadSingleChapter(initChapter.comicPathWord, uuid)
                mangaContent.value = ReaderContent(chapterLoader.snapshot(), MangaState(uuid, 0))
            }
        }
    }

    fun saveLocalChapterState(int: Int) {
        viewModelScope.launch {
            val currentChapter =
                list.find { it.uuid == getCurrentReaderState().uuid } ?: return@launch
            val newChapterTemp = currentChapter.copy(
                readIndex = int,
                isReadProgress = int != 0,
                isReadFinish = int == (currentChapter.size - 1)
            )
            repository.updateLocalChapter(newChapterTemp)
        }
    }

    private fun loadImp() {
        loadJob = loadJop(Dispatchers.Default) {
            list.forEach {
                chapters[it.uuid] = it
            }
            loadHistory(initChapter.comicPathWord)
            val mode = detectReaderMode()
            readerModel.emit(mode)
            chapterLoader.loadSingleChapter(initChapter.comicPathWord, state.value.uuid)
            onInfoChange()
            mangaContent.emit(ReaderContent(chapterLoader.snapshot(), state.value))
        }
    }

    /**
     * 检测漫画模式
     */
    private suspend fun detectReaderMode(): ReaderMode {
        val modeId = repository.getHistoryByMangaPathWord(
            currentPathWord
        )?.readerModeId
        return ReaderMode.idOf(modeId) ?: ReaderMode.valueOf(settingPref.readerMode)
    }

    private fun loadJop(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit,
    ) = viewModelScope.launch(context + createErrorHandler(), start) {
        _loadingCounter.emit(true)
        try {
            block()
        } finally {
            _loadingCounter.emit(false)
        }
    }

    private fun createErrorHandler() = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        if (throwable !is CancellationException) {
            _errorHandler.tryEmit(throwable)
        }
    }


}


