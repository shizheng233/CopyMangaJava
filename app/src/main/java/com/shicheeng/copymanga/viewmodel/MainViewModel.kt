package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shicheeng.copymanga.json.UpdateMetaDataJson
import kotlinx.coroutines.launch

class MainViewModel(private val updateMetaDataJson: UpdateMetaDataJson) : ViewModel() {

    val updateData = updateMetaDataJson.availableUpdateVersion()

    init {
        viewModelScope.launch {
            updateMetaDataJson.fetchUpdate()
        }
    }

}

class MainViewModelFactory(private val updateMetaDataJson: UpdateMetaDataJson) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(updateMetaDataJson) as T
        }
        throw IllegalArgumentException("CLASS NO MATCH")
    }

}