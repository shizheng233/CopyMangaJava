package com.shicheeng.copymanga.data.searchhistory

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchHistory(
    @PrimaryKey
    val word: String,
    val time:Long,
)