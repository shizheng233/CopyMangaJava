package com.shicheeng.copymanga.server.download.domin

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.shicheeng.copymanga.BuildConfig
import com.shicheeng.copymanga.data.MangaHistoryDataModel
import com.shicheeng.copymanga.data.MangaSortBean
import com.shicheeng.copymanga.data.info.Author
import com.shicheeng.copymanga.data.local.LocalChapter
import com.shicheeng.copymanga.data.local.LocalSavableMangaModel
import com.shicheeng.copymanga.util.add
import com.shicheeng.copymanga.util.toJsonArray

class DownloaderLocalIndex(source: String?) {

    constructor(source: () -> String?) : this(source())

    private val jsonObject = if (
        !source.isNullOrEmpty()
        && source.isNotEmpty()
        && JsonParser.parseString(source).isJsonObject
    ) {
        JsonParser.parseString(source).asJsonObject
    } else {
        JsonObject()
    }

    fun setMangaData(localSavableMangaModel: LocalSavableMangaModel, append: Boolean) {
        jsonObject.apply {
            addProperty("comic_id", localSavableMangaModel.mangaHistoryDataModel.comicUUID)
            addProperty("name", localSavableMangaModel.mangaHistoryDataModel.name)
            addProperty("cover", localSavableMangaModel.mangaHistoryDataModel.url)
            addProperty("description", localSavableMangaModel.mangaHistoryDataModel.mangaDetail)
            addProperty("state", localSavableMangaModel.mangaHistoryDataModel.mangaStatus)
            addProperty("alias", localSavableMangaModel.mangaHistoryDataModel.alias)
            addProperty("last_update", localSavableMangaModel.mangaHistoryDataModel.mangaLastUpdate)
            addProperty("name_chapter", localSavableMangaModel.mangaHistoryDataModel.nameChapter)
            addProperty("path_word", localSavableMangaModel.mangaHistoryDataModel.pathWord)
            addProperty(
                "manga_popular_num",
                localSavableMangaModel.mangaHistoryDataModel.mangaPopularNumber
            )
            addProperty("manga_region", localSavableMangaModel.mangaHistoryDataModel.mangaRegion)
            addProperty(
                "manga_reader_int",
                localSavableMangaModel.mangaHistoryDataModel.readerModeId
            )
            addProperty(
                "manga_state_id",
                localSavableMangaModel.mangaHistoryDataModel.mangaStatusId
            )
            addProperty(
                "time",
                localSavableMangaModel.mangaHistoryDataModel.time
            )
            add("tags") {
                localSavableMangaModel.mangaHistoryDataModel.themeList.toJsonArray(
                    header = { x -> x.pathName },
                    values = { y -> y.pathWord },
                    headerProperty = "name",
                    valuesProperty = "path_word"
                )
            }
            add("authors") {
                localSavableMangaModel.mangaHistoryDataModel.authorList.toJsonArray(
                    header = { x -> x.name },
                    headerProperty = "name",
                    valuesProperty = "path_word",
                    values = { y -> y.pathWord }
                )
            }
            if (!append || !jsonObject.has("chapters")) {
                add("chapters", JsonObject())
            }
            addProperty("app_version", BuildConfig.VERSION_NAME)
            addProperty("app_id", BuildConfig.APPLICATION_ID)
        }
    }

    fun getMangaData() = if (jsonObject.isEmpty) null else runCatching {
        MangaHistoryDataModel(
            name = jsonObject["name"].asString,
            comicUUID = jsonObject["comic_id"].asString,
            readerModeId = jsonObject["manga_reader_int"].asInt,
            mangaLastUpdate = jsonObject["last_update"].asString,
            mangaDetail = jsonObject["description"].asString,
            nameChapter = jsonObject["name_chapter"].asString,
            time = jsonObject["time"].asLong,
            alias = jsonObject["alias"].asString.takeIf { it.isNotBlank() && it.isNotEmpty() },
            pathWord = jsonObject["path_word"].asString,
            mangaPopularNumber = jsonObject["manga_popular_num"].asString,
            mangaRegion = jsonObject["manga_region"].asString,
            mangaStatus = jsonObject["state"].asString,
            mangaStatusId = jsonObject["manga_state_id"].asInt,
            themeList = jsonObject["tags"].asJsonArray.map {
                MangaSortBean(
                    it.asJsonObject["name"].asString,
                    it.asJsonObject["path_word"].asString
                )
            },
            url = jsonObject["cover"].asString,
            authorList = jsonObject["authors"].asJsonArray.map {
                Author(it.asJsonObject["name"].asString, it.asJsonObject["path_word"].asString)
            },
            isSubscribe = false,
            positionChapter = 0,
            positionPage = 0
        )
    }.getOrNull()


    fun getCoverEntry(): String? = jsonObject.has("cover_entry").let {
        if (it) jsonObject["cover_entry"].asString else null
    }

    fun setCoverEntry(name: String) {
        jsonObject.addProperty("cover_entry", name)
    }

    fun addChapter(chapter: LocalChapter, fileName: String?) {
        val jsonChapters = jsonObject["chapters"].asJsonObject
        if (!jsonChapters.has(chapter.uuid)) {
            jsonChapters.add(chapter.uuid) {
                JsonObject().also {
                    it.apply {
                        addProperty("chapter_name", chapter.name)
                        addProperty("comic_path_word", chapter.comicPathWord)
                        addProperty("chapter_is_reading", chapter.isReadProgress)
                        addProperty("chapter_comic_id", chapter.comicId)
                        addProperty("file_name", fileName)
                    }
                }
            }
        }
    }

    fun removeChapters(chapter: LocalChapter): Boolean {
        return jsonObject["chapters"].asJsonObject.remove(chapter.uuid) != null
    }

    fun getChapters(
        vararg uuid: String,
        localSavableMangaModel: LocalSavableMangaModel,
    ): List<LocalChapter> {
        return localSavableMangaModel.list.filter {
            uuid.contains(it.uuid)
        }
    }

    fun getChapterJson(uuid: String): JsonObject? {
        return if (jsonObject.isJsonObject && jsonObject.has(uuid)) {
            jsonObject[uuid].asJsonObject
        } else null
    }

    override fun toString(): String {
        return jsonObject.toString()
    }

    companion object {

        private const val OUT_PUT_FILE = ""

    }

}