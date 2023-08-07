package com.shicheeng.copymanga.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shicheeng.copymanga.data.MainPageDataModel
import com.shicheeng.copymanga.resposity.MangaMainPageRepository
import com.shicheeng.copymanga.util.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mangaMainPageRepository: MangaMainPageRepository,
) : ViewModel() {


    private val _uiState = MutableStateFlow<UIState<MainPageDataModel>>(UIState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() = viewModelScope.launch {
        _uiState.emit(UIState.Loading)
        try {
            val mainData = mangaMainPageRepository.fetchMainData()
            Log.d("TAG", "loadData: $mainData")
            _uiState.emit(UIState.Success(mainData))
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.emit(UIState.Error(e))
        }
    }




}

