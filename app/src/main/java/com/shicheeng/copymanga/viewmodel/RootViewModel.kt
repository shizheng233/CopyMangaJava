package com.shicheeng.copymanga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shicheeng.copymanga.json.UpdateMetaDataJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val updateMetaDataJson: UpdateMetaDataJson,
) : ViewModel() {

    val updateData = updateMetaDataJson.availableUpdateVersion()

    init {
        viewModelScope.launch {
            updateMetaDataJson.fetchUpdate()
        }
    }


}

