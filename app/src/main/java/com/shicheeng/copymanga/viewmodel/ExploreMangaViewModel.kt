package com.shicheeng.copymanga.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.shicheeng.copymanga.data.MangaSortBean
import com.shicheeng.copymanga.json.MangaSortJson
import com.shicheeng.copymanga.pagingsource.ExplorePagingSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExploreMangaViewModel : ViewModel() {

    sealed class UiState {
        data class Success(val list: List<MangaSortBean>) : UiState()
        object Loading : UiState()
        data class Error(val error: Exception) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    val order = MutableStateFlow("-popular")
    val themeType = MutableStateFlow("all")

    fun loadData() = viewModelScope.launch {
        _uiState.emit(UiState.Loading)
        try {
            val listTheme = MangaSortJson.getSort()
            _uiState.emit(UiState.Success(listTheme))
        } catch (e: Exception) {
            _uiState.emit(UiState.Error(e))
        }
    }

    val loadFilterResult = Pager(PagingConfig(21)) {
        Log.d("TAG", "loadFilterResult: ${order.value} -- ${themeType.value}")
        ExplorePagingSource(order.value, themeType.value)
    }.flow.cachedIn(viewModelScope)

}