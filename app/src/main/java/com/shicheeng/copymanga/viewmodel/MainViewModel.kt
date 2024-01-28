package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shicheeng.copymanga.resposity.LoginRepository
import com.shicheeng.copymanga.util.collectRepeatLifecycle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    loginRepository: LoginRepository,
) : ViewModel() {

    val loginInfoStatus = loginRepository.testLoginStatus()
        .distinctUntilChanged()
        .conflate()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )
    val showSnackBar = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            loginInfoStatus.collectLatest {
                showSnackBar.emit(it != null)
            }
        }
    }

    fun dismissShack() = viewModelScope.launch {
        showSnackBar.emit(false)
    }


}

