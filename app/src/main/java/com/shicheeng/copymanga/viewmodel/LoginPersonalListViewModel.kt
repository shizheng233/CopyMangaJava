package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shicheeng.copymanga.data.login.LocalLoginDataModel
import com.shicheeng.copymanga.resposity.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginPersonalListViewModel @Inject constructor(
    private val repository: LoginRepository,
) : ViewModel() {

    val personalList = repository
        .getAllLoginInstance()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    fun delete(localLoginDataModel: LocalLoginDataModel) = viewModelScope.launch {
        repository.deleteOneInstance(localLoginDataModel)
    }

    fun selectUUId(uuid: String) = viewModelScope.launch {
        repository.selectOne(uuid)
    }

}