package com.shicheeng.copymanga.util

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

fun SharedPreferences.booleanFlow(key: String): Flow<Boolean> {
    return keyChanger()
        .filter { !it.isNullOrBlank() && it.isNotEmpty() }
        .filterNotNull()
        .filter { it == key }
        .map {
            getBoolean(key, false)
        }
        .conflate()
}

/**
 * 返回一个字串符，但是是[Flow]。
 * 注意：如果需要使用[stateIn]方法最好加入初始时。
 *
 * @param key KEY.
 */
fun SharedPreferences.stringFlow(key: String): Flow<String?> {
    return keyChanger()
        .filter { !it.isNullOrBlank() && it.isNotEmpty() }
        .filterNotNull()
        .filter { it == key }
        .map {
            getString(key, null)
        }
        .conflate()
}

fun SharedPreferences.integerFlow(key: String, def: Int): Flow<Int> {
    return keyChanger()
        .filter { !it.isNullOrBlank() && it.isNotEmpty() }
        .filterNotNull()
        .filter { it == key }
        .map {
            getInt(key, def)
        }
        .conflate()
}

private fun SharedPreferences.keyChanger(): Flow<String?> {
    return callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            trySend(key)
        }
        registerOnSharedPreferenceChangeListener(listener)
        awaitClose {
            unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
}