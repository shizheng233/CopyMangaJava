package com.shicheeng.copymanga.fm.reader

import androidx.lifecycle.SavedStateHandle

class MangaLoader(
    savedStateHandle: SavedStateHandle,
) {

    val mangaPathWord = savedStateHandle.get<String>(MANGA_PATH_WORD) ?: NONE
    val mangaChapterUUID = savedStateHandle.get<String>(MANGA_UUID) ?: CHAPTER_NONE

    companion object {
        const val MANGA_PATH_WORD = "MANGA_PATH_WORD"
        const val MANGA_UUID = "MANGA_UUID"

        const val NONE = "NONE"
        const val CHAPTER_NONE = "CHAPTER_NONE"
    }

}