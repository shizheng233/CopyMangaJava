package com.shicheeng.copymanga.ui.screen.setting

import android.content.SharedPreferences
import androidx.core.content.edit
import com.shicheeng.copymanga.fm.reader.ReaderMode
import com.shicheeng.copymanga.util.ThemeMode
import com.shicheeng.copymanga.util.booleanFlow
import com.shicheeng.copymanga.util.stringFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingPref @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {

    val readerMode: String
        get() = sharedPreferences.getString(
            "pref_orientation_key",
            ReaderMode.NORMAL.name
        ) ?: ReaderMode.NORMAL.name

    fun setReaderMode(mode: ReaderMode) {
        sharedPreferences.edit {
            putString("pref_orientation_key", mode.name)
        }
    }

    val readerModeEntity = arrayOf(
        ReaderMode.NORMAL.name,
        ReaderMode.WEBTOON.name,
        ReaderMode.STANDARD.name
    )

    val themeModeEntity = arrayOf(
        ThemeMode.SYSTEM.name,
        ThemeMode.LIGHT.name,
        ThemeMode.DARK.name,
    )

    var useForeignApi: Boolean
        get() = sharedPreferences.getBoolean("pref_is_use_foreign_api", false)
        set(value) {
            sharedPreferences.edit {
                putBoolean("pref_is_use_foreign_api", value)
            }
        }

    var apiSelected: String
        get() = sharedPreferences.getString("key_api_header_select", "copymanga.net")
            ?: "copymanga.net"
        set(value) {
            sharedPreferences.edit {
                putString("key_api_header_select", value)
            }
        }

    private val _hyperTouch: MutableStateFlow<Boolean> =
        MutableStateFlow(sharedPreferences.getBoolean("key_touch_quick", false))
    val hyperTouch = _hyperTouch.asStateFlow()

    fun isUseHyperTouch(isUse: Boolean) {
        sharedPreferences.edit {
            putBoolean("key_touch_quick", isUse)
        }
        _hyperTouch.tryEmit(sharedPreferences.getBoolean("key_touch_quick", false))
    }

    private val _pauseUpdateDetector: MutableStateFlow<Boolean> =
        MutableStateFlow(sharedPreferences.getBoolean("disable_update", false))
    val pauseUpdateDetector = _pauseUpdateDetector.asStateFlow()

    fun isPauseDetectUpdate(isPause: Boolean) {
        sharedPreferences.edit {
            putBoolean("disable_update", isPause)
        }
        _pauseUpdateDetector.tryEmit(sharedPreferences.getBoolean("disable_update", isPause))
    }

    val enableComicsUpdate: MutableStateFlow<Boolean> =
        MutableStateFlow(sharedPreferences.getBoolean(KEY_ENABLE_COMIC_UPDATE, false))

    fun hasKey(key: String): Boolean {
        return sharedPreferences.contains(key)
    }

    fun enableComicsUpdateFetch(enable: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_ENABLE_COMIC_UPDATE, enable)
        }
        enableComicsUpdate.tryEmit(sharedPreferences.getBoolean(KEY_ENABLE_COMIC_UPDATE, false))
    }

    val timeInterval = MutableStateFlow(
        if (hasKey(KEY_COMIC_UPDATE_TIME)) {
            sharedPreferences.getInt(KEY_COMIC_UPDATE_TIME, 6)
        } else {
            6
        }
    )

    fun editTimeInterval(time: Int) {
        sharedPreferences.edit {
            putInt(KEY_COMIC_UPDATE_TIME, time)
        }
        timeInterval.tryEmit(sharedPreferences.getInt(KEY_COMIC_UPDATE_TIME, 6))
    }

    val updateConstant = MutableStateFlow(
        if (hasKey(KEY_COMIC_UPDATE_CONS)) {
            sharedPreferences.getStringSet(KEY_COMIC_UPDATE_CONS, setOf(IN_WIFI)) ?: setOf(IN_WIFI)
        } else {
            setOf(IN_WIFI)
        }
    )

    fun changeUpdateConstant(value: Set<String>) {
        sharedPreferences.edit {
            putStringSet(KEY_COMIC_UPDATE_CONS, value)
        }
        updateConstant.tryEmit(
            sharedPreferences.getStringSet(
                KEY_COMIC_UPDATE_CONS,
                setOf(IN_WIFI)
            ) ?: setOf(IN_WIFI)
        )
    }


    var appThemeMode
        get() = sharedPreferences.getString(KEY_APP_THEME, ThemeMode.SYSTEM.name)
            ?: ThemeMode.SYSTEM.name
        set(value) {
            sharedPreferences.edit {
                putString(KEY_APP_THEME, value)
            }
        }

    var cutoutDisplay
        get() = sharedPreferences.getBoolean(KEY_CUTOUT_DISPLAY, true)
        set(value) {
            sharedPreferences.edit {
                putBoolean(KEY_CUTOUT_DISPLAY, value)
            }
        }

    var cacheSize
        get() = sharedPreferences.getString(KEY_CACHE_SIZE, "400") ?: "400"
        set(value) {
            sharedPreferences.edit {
                putString(KEY_CACHE_SIZE, value)
            }
        }


    val loginPerson
        get() = sharedPreferences.getString(KEY_LOGIN_STATUS, null)

    val loginPersonalFlow
        get() = sharedPreferences.stringFlow(KEY_LOGIN_STATUS)

    fun selectedUUId(uuid: String?) {
        sharedPreferences.edit {
            putString(KEY_LOGIN_STATUS, uuid)
        }
    }

    val webReadPoint
        get() = sharedPreferences.getBoolean(
            KEY_ENABLE_WEB_READ_POINT,
            false
        )


    val webReadPointFlow
        get() = sharedPreferences.booleanFlow(KEY_ENABLE_WEB_READ_POINT)


    fun enableWebReadPoint(boolean: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_ENABLE_WEB_READ_POINT, boolean)
        }
    }

    companion object {
        const val KEY_ENABLE_COMIC_UPDATE = "KEY_ENABLE_COMIC_UPDATE"
        const val KEY_COMIC_UPDATE_TIME = "KEY_COMIC_UPDATE_TIME"
        const val KEY_COMIC_UPDATE_CONS = "KEY_COMIC_UPDATE_CONS"
        const val KEY_APP_THEME = "KEY_APP_THEME"
        const val KEY_CUTOUT_DISPLAY = "KEY_CUTOUT_DISPLAY"
        const val KEY_CACHE_SIZE = "KEY_CACHE_SIZE"
        const val KEY_LOGIN_STATUS = "KEY_LOGIN_STATUS"
        const val KEY_ENABLE_WEB_READ_POINT = "KEY_ENABLE_WEB_READ_POINT"
    }


}


const val IN_WIFI = "IN_WIFI"
const val IN_CHARGING = "IN_CHARGING"
const val IN_BATTERY_NOT_LOW = "IN_BATTERY_NOT_LOW"


