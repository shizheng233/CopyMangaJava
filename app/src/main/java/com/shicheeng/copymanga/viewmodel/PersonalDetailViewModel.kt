package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shicheeng.copymanga.resposity.LoginDetailRepository
import com.shicheeng.copymanga.util.RetryTrigger
import com.shicheeng.copymanga.util.UIState
import com.shicheeng.copymanga.util.retryableFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PersonalDetailViewModel @Inject constructor(
    private val loginDetailRepository: LoginDetailRepository,
) : ViewModel() {

    private val retryTrigger = RetryTrigger()

    @OptIn(FlowPreview::class)
    val data = retryableFlow(retryTrigger) {
        loginDetailRepository.detail()
            .onStart {
                UIState.Loading
            }
    }.stateIn(
        scope = viewModelScope,
        initialValue = UIState.Loading,
        started = SharingStarted.Eagerly
    )

    fun retry() = retryTrigger.retry()

}