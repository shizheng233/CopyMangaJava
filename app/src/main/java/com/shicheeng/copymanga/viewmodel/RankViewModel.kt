package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shicheeng.copymanga.resposity.MangaRankRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RankViewModel @Inject constructor(
    rankRepository: MangaRankRepository,
) : ViewModel() {

    val dayRank = rankRepository.fetchMangaRank("day").cachedIn(viewModelScope)
    val weekRank = rankRepository.fetchMangaRank("week").cachedIn(viewModelScope)
    val monthRank = rankRepository.fetchMangaRank("month").cachedIn(viewModelScope)
    val totalRank = rankRepository.fetchMangaRank("total").cachedIn(viewModelScope)

}