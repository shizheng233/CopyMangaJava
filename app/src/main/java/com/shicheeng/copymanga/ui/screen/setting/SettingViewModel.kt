package com.shicheeng.copymanga.ui.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shicheeng.copymanga.fm.reader.ReaderMode
import com.shicheeng.copymanga.resposity.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingPref: SettingPref,
    private val repository: LoginRepository,
) : ViewModel() {

    private val _readerMode = MutableStateFlow(settingPref.readerMode)
    val readerMode = _readerMode.asStateFlow()

    private val _useForeignRequest = MutableStateFlow(settingPref.useForeignApi)
    val useForeignRequest = _useForeignRequest.asStateFlow()

    private val _apiSelected = MutableStateFlow(settingPref.apiSelected)
    val apiSelected = _apiSelected.asStateFlow()

    val isTurn = settingPref.enableComicsUpdate.asStateFlow()
    val timeInterval = settingPref.timeInterval.asStateFlow()
    val updateConstants = settingPref.updateConstant.asStateFlow()
    private val _themeModeName = MutableStateFlow(settingPref.appThemeMode)
    val themeModeName = _themeModeName.asStateFlow()

    private val _cutoutDisplay = MutableStateFlow(settingPref.cutoutDisplay)
    val cutoutDisplay = _cutoutDisplay.asStateFlow()

    private val _cacheSize = MutableStateFlow(settingPref.cacheSize)
    val cacheSize = _cacheSize.asStateFlow()

    val webPoint = settingPref.webReadPointFlow
        .stateIn(
            scope = viewModelScope,
            initialValue = settingPref.webReadPoint,
            started = SharingStarted.Eagerly
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val name = settingPref.loginPersonalFlow
        .stateIn(
            started = SharingStarted.Eagerly,
            scope = viewModelScope,
            initialValue = settingPref.loginPerson
        )
        .filter { !it.isNullOrBlank() || !it.isNullOrEmpty() }
        .filterNotNull()
        .flatMapLatest {
            repository.getUserByUUid(it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )


    fun setReaderMode(readerMode: ReaderMode) {
        settingPref.setReaderMode(readerMode)
        _readerMode.tryEmit(settingPref.readerMode)
    }

    fun setThemeMode(themeModeName: String) {
        settingPref.appThemeMode = themeModeName
        _themeModeName.tryEmit(settingPref.appThemeMode)
    }

    fun isUseForeignRequest(isUse: Boolean) {
        settingPref.useForeignApi = isUse
        _useForeignRequest.tryEmit(settingPref.useForeignApi)
    }

    fun selectApi(api: String) {
        settingPref.apiSelected = api
        _apiSelected.tryEmit(settingPref.apiSelected)
    }

    fun switchCutoutDisplay(enable: Boolean) = viewModelScope.launch {
        settingPref.cutoutDisplay = enable
        _cutoutDisplay.emit(settingPref.cutoutDisplay)
    }

    fun setCacheSize(size: String) = viewModelScope.launch {
        settingPref.cacheSize = size
        _cacheSize.emit(size)
    }

}