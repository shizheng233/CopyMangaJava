package com.shicheeng.copymanga.util

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser

val String.asJsonElementOrNull: JsonElement?
    get() {
        return if (this.isEmpty() || this.isNotBlank()) {
            null
        } else {
            val element = JsonParser.parseString(this)
            if (element.isJsonNull) null
            else element
        }
    }

val JsonElement.asStringOrNull: String?
    get() {
        return if (this.isJsonNull) {
            null
        } else {
            this.asString
        }
    }

fun JsonObject.getOr(member: String, other: () -> JsonElement): JsonElement {
    return if (this.has(member) && !this.get(member).isJsonNull) {
        this.get(member)
    } else {
        other()
    }
}

fun JsonObject.orEmptyJsonObject(): JsonObject = if (this.isEmpty) JsonObject() else this