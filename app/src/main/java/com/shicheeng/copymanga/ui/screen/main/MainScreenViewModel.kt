package com.shicheeng.copymanga.ui.screen.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor() : ViewModel() {

    private val _topWord = MutableStateFlow<String?>(null)
    private val _orderWord = MutableStateFlow<String?>(null)
    private val _themeWord = MutableStateFlow<String?>(null)

    val order = _orderWord.asStateFlow()
    val top = _topWord.asStateFlow()
    val theme = _themeWord.asStateFlow()


    fun addOrder(orderPathWord: String) {
        _orderWord.tryEmit(orderPathWord)
        _topWord.tryEmit(null)
    }

    fun addTop(topWord: String) {
        _topWord.tryEmit(topWord)
        _orderWord.tryEmit(null)
    }

}