package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shicheeng.copymanga.resposity.LoginRepository
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PersonalViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    settingPref: SettingPref,
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val user = settingPref.loginPersonalFlow
        .stateIn(
            scope = viewModelScope,
            initialValue = settingPref.loginPerson,
            started = SharingStarted.Eagerly
        )
        .filter { !it.isNullOrBlank() && it.isNotEmpty() }
        .filterNotNull()
        .flatMapLatest {
            loginRepository.getUserByUUid(it)
        }.stateIn(
            scope = viewModelScope,
            initialValue = null,
            started = SharingStarted.Eagerly
        )
}

