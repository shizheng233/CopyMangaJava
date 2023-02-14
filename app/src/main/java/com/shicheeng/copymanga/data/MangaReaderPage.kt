package com.shicheeng.copymanga.data

data class MangaReaderPage(
    val url: String,
    val uuid: String?,
    val index: Int,
    val urlHashCode: Int = url.hashCode(),
) {
    override fun hashCode(): Int {
        return url.hashCode() + uuid.hashCode() * 212
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MangaReaderPage

        if (url != other.url) return false
        if (uuid != other.uuid) return false
        if (index != other.index) return false
        if (urlHashCode != other.urlHashCode) return false

        return true
    }


}