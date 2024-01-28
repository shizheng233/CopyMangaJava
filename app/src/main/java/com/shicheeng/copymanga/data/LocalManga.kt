package com.shicheeng.copymanga.data

import com.shicheeng.copymanga.data.local.LocalSavableMangaModel
import java.io.File

data class LocalManga(
    val localSavableMangaModel: LocalSavableMangaModel,
    val file:File,
)
