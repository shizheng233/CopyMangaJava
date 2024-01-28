package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shicheeng.copymanga.resposity.ComicCommentRepository
import com.shicheeng.copymanga.resposity.LoginTokenRepository
import com.shicheeng.copymanga.util.RetryTrigger
import com.shicheeng.copymanga.util.SendUIState
import com.shicheeng.copymanga.util.retryableFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CommentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ComicCommentRepository,
    private val loginTokenRepository: LoginTokenRepository,
) : ViewModel() {

    private val uuid: String? = savedStateHandle["uuid_comic"]
    private val _comicUUID = MutableStateFlow(uuid)

    private val _comicText = MutableStateFlow("")

    val loginIsExpired = loginTokenRepository.isExpiredFlow
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            initialValue = loginTokenRepository.isExpired,
            started = SharingStarted.Eagerly
        )

    val retry = RetryTrigger()
    private val commentPushEvent = combine(_comicUUID, _comicText) { uuid, text ->
        if (uuid != null) CombineComicDataModel(uuid, text) else null
    }.filterNotNull().filter {
        it.ensureTextNoNull()
    }.flatMapLatest {
        repository.push(it.uuid, it.text)
    }

    @OptIn(FlowPreview::class)
    val commentPush = retryableFlow(retry) {
        commentPushEvent
    }.stateIn(
        scope = viewModelScope,
        initialValue = SendUIState.Idle,
        started = SharingStarted.Eagerly
    )


    val comments = _comicUUID
        .filter { !it.isNullOrBlank() && it.isNotEmpty() }
        .filterNotNull()
        .flatMapLatest {
            repository.loadComment(it)
        }.cachedIn(viewModelScope)


    /**
     * 发送消息
     * @param text 消息文本
     * @author ShihCheeng
     */
    fun sendComment(text: String) = viewModelScope.launch {
        _comicText.emit(text)
    }

    data class CombineComicDataModel(
        val uuid: String,
        val text: String,
    ) {
        fun ensureTextNoNull() = uuid.isNotEmpty()
                && uuid.isNotBlank()
                && text.isNotBlank()
                && text.isNotEmpty()
    }

}