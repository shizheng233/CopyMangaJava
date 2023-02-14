package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonArray
import com.shicheeng.copymanga.data.BannerList
import com.shicheeng.copymanga.json.MainBannerJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    sealed class UiState {
        data class Success(val data: HomeData) : UiState()
        object Loading : UiState()
        data class Error(val error: Exception) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadData() = viewModelScope.launch {
        _uiState.emit(UiState.Loading)
        try {
            withContext(Dispatchers.Default) {
                val mainJson = MainBannerJson.mainList
                val listBanner = MainBannerJson.getBannerMain(mainJson)
                val recMange = MainBannerJson.getRecMain(mainJson)
                val rankMange = MainBannerJson.getDayRankMain(mainJson)
                val hotMange = MainBannerJson.getHotMain(mainJson)
                val newMange = MainBannerJson.getNewMain(mainJson)
                val finishMange = MainBannerJson.getFinishMain(mainJson)
                val homeData =
                    HomeData(listBanner, recMange, rankMange, hotMange, newMange, finishMange)
                _uiState.emit(UiState.Success(homeData))
            }
        } catch (e: Exception) {
            _uiState.emit(UiState.Error(e))
        }
    }

}

data class HomeData(
    val listBanner: ArrayList<BannerList>,
    val listRecommend: JsonArray,
    val listRank: HashMap<Int, JsonArray>,
    val listHot: JsonArray,
    val listNewest: JsonArray,
    val listFinished: JsonArray,
)