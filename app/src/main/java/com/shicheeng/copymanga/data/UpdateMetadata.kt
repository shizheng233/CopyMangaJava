package com.shicheeng.copymanga.data


data class VersionId(
    val major: Int,
    val minor: Int,
    val build: Int,
    val type: String,
    val typeRNum: Int,
) : Comparable<VersionId> {

    override fun compareTo(other: VersionId): Int {
        var diff = major.compareTo(other.major)
        if (diff != 0) return diff
        diff = minor.compareTo(other.minor)
        if (diff != 0) return diff
        diff = build.compareTo(other.build)
        if (diff != 0) return diff
        diff = typeCompareWeight(type).compareTo(typeCompareWeight(other.type))
        if (diff != 0) return diff
        return typeRNum.compareTo(other.typeRNum)
    }

    private fun typeCompareWeight(type: String): Int = when (type) {
        "FIX" -> 8
        "PATCH" -> 4
        "" -> 2
        else -> 0
    }

}

data class VersionUnit(
    val id: Long,
    val htmlUrl: String,
    val versionName: String,
    val apkUrl: String,
    val apkSize: Long,
    val description: String,
    val time: String,
    val versionId: VersionId = versionId(versionName),
)

fun versionId(nameTag: String): VersionId {
    val part = nameTag.substringBefore("-").split(".")
    val name = nameTag.substringAfter("-", "")
    return VersionId(
        major = part.getOrNull(0)?.toIntOrNull() ?: 0,
        minor = part.getOrNull(1)?.toIntOrNull() ?: 0,
        build = part.getOrNull(2)?.toIntOrNull() ?: 0,
        type = name.filter(Char::isUpperCase),
        typeRNum = name.filter(Char::isDigit).toIntOrNull() ?: 0
    )
}

val VersionId.isNormal get() = type.isEmpty()