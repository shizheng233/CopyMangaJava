package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shicheeng.copymanga.json.UpdateMetaDataJson
import com.shicheeng.copymanga.resposity.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val updateMetaDataJson: UpdateMetaDataJson,
    loginRepository: LoginRepository,
) : ViewModel() {

    val updateData = updateMetaDataJson.availableUpdateVersion()
    val loginInfoStatus = loginRepository.testLoginStatus()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    init {
        viewModelScope.launch {
            updateMetaDataJson.fetchUpdate()
        }
    }

}

