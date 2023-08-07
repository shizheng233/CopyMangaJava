package com.shicheeng.copymanga.viewmodel

import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.shicheeng.copymanga.fm.reader.ReaderMode
import com.shicheeng.copymanga.resposity.MangaHistoryRepository
import com.shicheeng.copymanga.resposity.MangaInfoRepository
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import com.shicheeng.copymanga.util.FileUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.collections.set
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @param pagerLoader 为创建多个实例，请勿使用[Singleton]注解。并使用[Provider]多次提供。
 */
class ReaderViewModel @AssistedInject constructor(
    private val repository: MangaHistoryRepository,
    pagerLoader: Provider<PagerLoader>,
    private val settingPref: SettingPref,
    @Assisted private val fileUtil: FileUtil,
    @Assisted("path_word") private val currentPathWord: String?,
    @Assisted("uuid") private val currentChapterUUID: String?,
    private val mangaInfoRepository: MangaInfoRepository,
) : ViewModel() {


    private val list by lazy {
        runBlocking {
            mangaInfoRepository.fetchMangaChapters(
                pathWord = requireNotNull(currentPathWord)
            )
        }
    }

    private val initChapter = list.find { x -> x.uuid == currentChapterUUID } ?: list[0]
    private val chapterLoader = ChapterLoader(fileUtil, mangaInfoRepository)
    private var loadJob: Job? = null
    private val chapters: LinkedHashMap<String, LocalChapter> get() = chapterLoader.chapters

    private val _loadingCounter = MutableLiveData(false)
    val loadingCounter: LiveData<Boolean> get() = _loadingCounter

    private val _errorHandler = MutableLiveData<Throwable>()
    val errorHandler: LiveData<Throwable> get() = _errorHandler

    val information = MutableLiveData<ReaderState>(null)
    private val historyData = MutableLiveData<MangaHistoryDataModel?>(null)
    val state = MutableStateFlow(initChapter.toMangaState())
    val mangaContent = MutableLiveData(ReaderContent(emptyList(), null))
    val readerModel = MutableLiveData<ReaderMode>()
    val pagerLoaderIn: PagerLoader = pagerLoader.get()

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
        pagerLoaderIn.close()
        super.onCleared()
    }

    @AnyThread
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
        information.postValue(readerState)
        viewModelScope.launch {
            val newHistoryData = historyData.value
                ?.copy(
                    positionPage = state.page,
                    positionChapter = positionChapter,
                    time = System.currentTimeMillis()
                )
            if (newHistoryData != null) {
                repository.update(newHistoryData)
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
        mangaContent.value?.run {
            mangaContent.value = copy(state = getCurrentReaderState())
        }
        val renew = historyData.value?.copy(readerModeId = readerMode.id) ?: return@launch
        repository.update(renew)
        historyData.postValue(renew)
    }

    fun switchChapter(uuid: String?) {
        if (uuid != null) {
            val prevJob = loadJob
            loadJob = loadJop(Dispatchers.Default) {
                prevJob?.cancelAndJoin()
                mangaContent.postValue(ReaderContent(emptyList(), null))
                chapterLoader.loadSingleChapter(initChapter.comicPathWord, uuid)
                mangaContent.postValue(ReaderContent(chapterLoader.snapshot(), MangaState(uuid, 0)))
            }
        }
    }

    fun saveLocalChapterState(int: Int) {
        viewModelScope.launch {
            val currentChapter =
                list.find { it.uuid == getCurrentReaderState().uuid } ?: return@launch
            val newChapterTemp = currentChapter.copy(
                readIndex = int,
                isReadProgress = int != 0
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
            readerModel.postValue(mode)
            chapterLoader.loadSingleChapter(initChapter.comicPathWord, state.value.uuid)
            onInfoChange()
            mangaContent.postValue(ReaderContent(chapterLoader.snapshot(), state.value))
        }
    }

    /**
     * 检测漫画模式
     */
    private suspend fun detectReaderMode(): ReaderMode {
        val modeId = repository.getHistoryByMangaPathWord(
            currentPathWord ?: initChapter.comicPathWord
        )?.readerModeId
        return ReaderMode.idOf(modeId) ?: ReaderMode.valueOf(settingPref.readerMode)
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

    @AssistedFactory
    interface Factory {
        fun create(
            fileUtil: FileUtil,
            @Assisted("path_word") currentPathWord: String?,
            @Assisted("uuid") currentChapterUUID: String?,
        ): ReaderViewModel
    }

}


