package com.shicheeng.copymanga.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shicheeng.copymanga.resposity.LoginRepository
import com.shicheeng.copymanga.util.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository,
) : ViewModel() {

    private val _username = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val loginStatus = combine(_username, _password) { u, p ->
        UPPackage(u, p)
    }.filter {
        it.notEmptyOrBlank()
    }.flatMapLatest {
        repository.login(username = it.username, password = it.password)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LoginState.NoStatus
    )

    fun loginUP(username: String, password: String) = viewModelScope.launch {
        _username.emit(username)
        _password.emit(password)
    }

    data class UPPackage(
        val username: String,
        val password: String,
    ) {
        fun notEmptyOrBlank(): Boolean {
            return username.isNotBlank()
                    && password.isNotBlank()
                    && username.isNotEmpty()
                    && password.isNotEmpty()
        }
    }

}