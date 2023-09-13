package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shicheeng.copymanga.resposity.WebShelfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebShelfViewModel @Inject constructor(
    webShelfRepository: WebShelfRepository,
) : ViewModel() {

    val data = webShelfRepository
        .loadWebShelf()
        .cachedIn(
            scope = viewModelScope
        )

}