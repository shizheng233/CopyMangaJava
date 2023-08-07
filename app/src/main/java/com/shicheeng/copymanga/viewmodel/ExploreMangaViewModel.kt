package com.shicheeng.copymanga.viewmodel

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shicheeng.copymanga.data.MangaSortBean
import com.shicheeng.copymanga.data.finished.Item
import com.shicheeng.copymanga.json.MangaSortJson
import com.shicheeng.copymanga.resposity.MangaFilterRepository
import com.shicheeng.copymanga.resposity.logD
import com.shicheeng.copymanga.util.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ExploreMangaViewModel @Inject constructor(
    private val repository: MangaFilterRepository,
) : ViewModel() {


    private val _uiState = MutableStateFlow<UIState<List<MangaSortBean>>>(UIState.Loading)
    val uiState = _uiState.asStateFlow()

    val order = MutableStateFlow<String?>(null)
    val themeType = MutableStateFlow<String?>(null)
    val top = MutableStateFlow<String?>(null)

    val loadFilterResult: Flow<PagingData<Item>> = combine(order, themeType, top) { t1, t2, t3 ->
        FilterKeyModel(order = t1, theme = t2, top = t3)
    }.flatMapLatest {
        repository.filterMangas(top = it.top, theme = it.theme, ordering = it.order)
    }.cachedIn(viewModelScope)


    private val _showBottomSheet = MutableStateFlow(MangaSortJson.ORDER)
    val showBottomSheet = _showBottomSheet.asStateFlow()

    init {
        loadData()
    }

    fun loadData() = viewModelScope.launch {
        _uiState.emit(UIState.Loading)
        try {
            val listTheme = repository.theme()
            _uiState.emit(UIState.Success(listTheme))
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.emit(UIState.Error(e))
        }
    }

    fun filterOn(
        order: String? = null,
        theme: String? = null,
        top: String? = null,
    ) = viewModelScope.launch {
        this@ExploreMangaViewModel.order.emit(order)
        this@ExploreMangaViewModel.themeType.emit(theme)
        this@ExploreMangaViewModel.top.emit(top)
    }

    fun showThemeFilterList() {
        _showBottomSheet.tryEmit(MangaSortJson.THEME)

    }

    fun showTopFilterList() {
        _showBottomSheet.tryEmit(MangaSortJson.PATH)

    }

    fun showOrderFilterList() {
        _showBottomSheet.tryEmit(MangaSortJson.ORDER)
    }

    data class FilterKeyModel(
        val order: String?,
        val theme: String?,
        val top: String?,
    )


}