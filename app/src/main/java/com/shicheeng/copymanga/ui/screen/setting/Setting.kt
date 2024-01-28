package com.shicheeng.copymanga.ui.screen.setting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.shicheeng.copymanga.LocalSettingPreference
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.fm.domain.makeDirIfNoExist
import com.shicheeng.copymanga.fm.reader.ReaderMode
import com.shicheeng.copymanga.server.work.DetectMangaUpdateWork
import com.shicheeng.copymanga.ui.screen.compoents.PlainButton
import com.shicheeng.copymanga.ui.screen.compoents.VerticalFastScroller
import com.shicheeng.copymanga.util.FileCacheUtils
import com.shicheeng.copymanga.util.ThemeMode
import com.shicheeng.copymanga.util.copy
import com.shicheeng.copymanga.util.setSystemNightMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SettingScreen(
    viewModel: SettingViewModel = hiltViewModel(),
    onNavigateClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onWorkerClick: () -> Unit,
    onUserClick: () -> Unit,
    onAboutClick: () -> Unit,
) {

    val settingPref = LocalSettingPreference.current
    val layoutDirection = LocalLayoutDirection.current
    val context = LocalContext.current
    val resource = context.resources
    val exCacheDir = context.cacheDir
    val cache = getFileCacheDir(context)
    val coroutine = rememberCoroutineScope()
    var cacheDirSummary by remember { mutableStateOf(cache.getSize()) }
    var cachePageSize by remember { mutableStateOf(exCacheDir.getSize()) }
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val readerMode by viewModel.readerMode.collectAsState()
    val isUseForeignReq by viewModel.useForeignRequest.collectAsState()
    val apiSelect by viewModel.apiSelected.collectAsState()
    val turnOn by viewModel.isTurn.collectAsState()
    val lazyListState = rememberLazyListState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.setting)) },
                navigationIcon = {
                    PlainButton(
                        id = R.string.back_to_up,
                        drawableRes = R.drawable.ic_arrow_back
                    ) {
                        onNavigateClick()
                    }
                },
                scrollBehavior = topAppBarScrollBehavior
            )
        }
    ) { padding ->
        VerticalFastScroller(
            listState = lazyListState,
            topContentPadding = padding.calculateTopPadding()
        ) {
            LazyColumn(
                contentPadding = padding.copy(
                    layoutDirection = layoutDirection,
                    bottom = padding.calculateBottomPadding()
                ),
                state = lazyListState,
                modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            ) {
                groupText(R.string.login_text)
                item {
                    val model by viewModel.name.collectAsState()
                    Preference(
                        title = stringResource(id = R.string.login_personal),
                        summary = model?.nikeName ?: stringResource(R.string.no_login),
                        leaderIconRes = R.drawable.ic_person_center
                    ) {
                        onUserClick()
                    }
                }
                item {
                    val webPoint by viewModel.webPoint.collectAsState()
                    SwitchPreference(
                        title = stringResource(R.string.web_point_enable),
                        summary = stringResource(R.string.web_point_enable_summary),
                        selectValue = webPoint,
                        leaderIconRes = R.drawable.baseline_webhook_24,
                        onClick = {
                            settingPref.enableWebReadPoint(it)
                        }
                    )
                }
                item {
                    WarningPreference(supportText = stringResource(R.string.web_point_warning))
                }
                item {
                    TipPreference(supportText = stringResource(R.string.web_point_tip))
                }
                groupText(R.string.pref_main)
                item {
                    val array1 =
                        rememberSaveable { resource.getStringArray(R.array.orientation_array) }
                    ListPreference(
                        title = stringResource(id = R.string.reader_mode_tip),
                        summary = array1[settingPref.readerModeEntity.indexOf(readerMode)],
                        dialogSupportedText = stringResource(id = R.string.swith_reader_mode_dialog_summary),
                        array = array1,
                        selectValue = array1[settingPref.readerModeEntity.indexOf(readerMode)],
                        arrayValue = settingPref.readerModeEntity,
                        leaderIconRes = R.drawable.outline_chrome_reader_mode_24
                    ) { index, array ->
                        viewModel.setReaderMode(ReaderMode.valueOf(array[index]))
                    }
                }
                item {
                    SwitchPreference(
                        title = stringResource(id = R.string.use_forgin_region),
                        summary = stringResource(id = R.string.use_forgin_region_summary),
                        selectValue = isUseForeignReq,
                        leaderIconRes = R.drawable.outline_switch_access_shortcut_24
                    ) {
                        viewModel.isUseForeignRequest(isUse = it)
                    }
                }
                item {
                    TipPreference(supportText = stringResource(id = R.string.use_forgin_region_tip))
                }
                item {
                    ListPreference(
                        title = stringResource(id = R.string.select_api_header),
                        summary = apiSelect,
                        dialogSupportedText = stringResource(id = R.string.switch_api_dialog_summary),
                        array = resource.getStringArray(R.array.api_header),
                        selectValue = apiSelect,
                        arrayValue = resource.getStringArray(R.array.api_header_value),
                        leaderIconRes = R.drawable.outline_cell_wifi_24,
                    ) { index, array ->
                        viewModel.selectApi(array[index])
                    }
                }
                item {
                    SwitchPreference(
                        title = stringResource(id = R.string.subscribe_for_updates),
                        summary = stringResource(id = R.string.subscribe_for_updates_summary),
                        selectValue = turnOn,
                        leaderIconRes = R.drawable.baseline_rss_feed_24
                    ) {
                        settingPref.enableComicsUpdateFetch(it)
                        DetectMangaUpdateWork.readyToStart(
                            isEnable = it,
                            context = context,
                            settingPref = settingPref
                        )
                    }
                }
                item {
                    TipPreference(supportText = stringResource(R.string.tip_subscribe))
                }
                item(key = "KEY_EDIT_TEXT_FOR_UPDATE_DETECT") {
                    AnimatedVisibility(
                        visible = turnOn,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut(),
                    ) {
                        val time by viewModel.timeInterval.collectAsState()
                        EditTextPreference(
                            title = stringResource(id = R.string.time_detect),
                            summary = time.toString(),
                            dialogSupportedText = stringResource(id = R.string.time_detect_summary),
                            originalValue = time.toString(),
                            leaderIconRes = R.drawable.outline_timer_24,
                            modifier = Modifier.animateItemPlacement(),
                            onInput = {
                                settingPref.editTimeInterval(it.toInt())
                                DetectMangaUpdateWork.readyToStart(
                                    isEnable = settingPref.enableComicsUpdate.value,
                                    context = context,
                                    settingPref = settingPref,
                                    takeInterval = it.toInt()
                                )
                            }
                        )
                    }
                }
                item {
                    val dataMap = mapOf(
                        IN_WIFI to stringResource(id = R.string.only_wifi),
                        IN_CHARGING to stringResource(id = R.string.only_charging),
                        IN_BATTERY_NOT_LOW to stringResource(id = R.string.low_power)
                    )
                    val selected by viewModel.updateConstants.collectAsState()
                    MutableSelectListPreference(
                        title = stringResource(id = R.string.update_constant),
                        dialogSupportedText = stringResource(id = R.string.update_constant_support_text),
                        mapValue = dataMap,
                        selectValue = selected,
                        leaderIconRes = R.drawable.outline_auto_mode_24,
                        summaryProvider = { valueMap ->
                            valueMap.filter {
                                selected.contains(it.key)
                            }.values.takeIf { it.isNotEmpty() }
                                ?.joinToString()
                                ?: stringResource(id = R.string.no_select_constants)
                        },
                        onOK = {
                            settingPref.changeUpdateConstant(it)
                            ContextCompat.getMainExecutor(context).execute {
                                DetectMangaUpdateWork.readyToStart(
                                    isEnable = settingPref.enableComicsUpdate.value,
                                    context = context,
                                    settingPref = settingPref
                                )
                            }
                        }
                    )
                }
                groupText(R.string.system)
                item {
                    val themeName by viewModel.themeModeName.collectAsState()
                    val array2 = arrayOf(
                        stringResource(id = R.string.theme_with_system),
                        stringResource(id = R.string.theme_light),
                        stringResource(id = R.string.theme_dark),
                    )
                    ListPreference(
                        title = stringResource(id = R.string.theme_mode),
                        summary = when (ThemeMode.valueOf(themeName)) {
                            ThemeMode.SYSTEM -> stringResource(id = R.string.theme_with_system)
                            ThemeMode.DARK -> stringResource(id = R.string.theme_dark)
                            ThemeMode.LIGHT -> stringResource(id = R.string.theme_light)
                        },
                        dialogSupportedText = stringResource(id = R.string.theme_mode_support_text),
                        array = array2,
                        selectValue = array2[settingPref.themeModeEntity.indexOf(themeName)],
                        arrayValue = settingPref.themeModeEntity,
                        leaderIconRes = R.drawable.outline_contrast_24,
                        onItemClick = { i: Int, strings: Array<String> ->
                            viewModel.setThemeMode(strings[i])
                            setSystemNightMode(ThemeMode.valueOf(strings[i]))
                            (context as Activity).recreate()
                        }
                    )
                }
                item {
                    val isUse by settingPref.hyperTouch.collectAsState()
                    SwitchPreference(
                        title = stringResource(id = R.string.quick_touch),
                        summary = stringResource(id = R.string.quick_touch_summary),
                        selectValue = isUse,
                        leaderIconRes = R.drawable.outline_touch_app_24,
                        onClick = settingPref::isUseHyperTouch
                    )
                }
                item {
                    val enable by viewModel.cutoutDisplay.collectAsState()
                    SwitchPreference(
                        title = stringResource(id = R.string.cut_out_display),
                        summary = stringResource(id = R.string.cut_out_display_summary),
                        selectValue = enable,
                        leaderIconRes = R.drawable.baseline_content_cut_24,
                        onClick = viewModel::switchCutoutDisplay
                    )
                }
                item {
                    val isPause by settingPref.pauseUpdateDetector.collectAsState()
                    SwitchPreference(
                        title = stringResource(id = R.string.disable_update_detect),
                        summary = stringResource(id = R.string.disable_update_summary),
                        selectValue = isPause,
                        leaderIconRes = R.drawable.outline_update_disabled_24,
                        onClick = settingPref::isPauseDetectUpdate
                    )
                }
                item {
                    val cacheSize by viewModel.cacheSize.collectAsState()
                    EditTextPreference(
                        title = stringResource(R.string.cache_size_setting),
                        summary = "$cacheSize MB",
                        dialogSupportedText = stringResource(R.string.cache_size_setting_supporting_text),
                        originalValue = cacheSize,
                        leaderIconRes = R.drawable.baseline_cached_24,
                        onInput = {
                            viewModel.setCacheSize(it)
                        }
                    )
                }
                item {
                    Preference(
                        title = stringResource(id = R.string.clear_cache),
                        summary = cacheDirSummary,
                        leaderIconRes = R.drawable.outline_clean_hands_24
                    ) {
                        coroutine.launch {
                            clearCache(cache) {
                                cacheDirSummary = it
                            }
                        }
                    }
                }
                item {
                    Preference(
                        title = stringResource(id = R.string.clear_pager_cache),
                        summary = cachePageSize,
                        leaderIconRes = R.drawable.outline_cleaning_services_24
                    ) {
                        coroutine.launch {
                            clearCache(exCacheDir) {
                                cachePageSize = it
                            }
                        }
                    }
                }
                item {
                    Preference(
                        title = stringResource(R.string.work_information),
                        summary = stringResource(R.string.work_info_summary),
                        leaderIconRes = R.drawable.outline_work_history_24,
                        onClick = onWorkerClick
                    )
                }
                groupText(R.string.download_manga)
                item {
                    val onlyOnWifi by viewModel.onlyInWifi.collectAsState()
                    SwitchPreference(
                        title = stringResource(R.string.only_on_wifi),
                        summary = stringResource(R.string.only_on_wifi_summary),
                        selectValue = onlyOnWifi,
                        leaderIconRes = R.drawable.outline_wifi_lock_24,
                        onClick = {
                            viewModel.changeDownloadConstants(it)
                        }
                    )
                }
                item {
                    Preference(
                        title = stringResource(R.string.download_list),
                        summary = stringResource(id = R.string.see_the_download),
                        leaderIconRes = R.drawable.outline_file_download_24,
                        onClick = onDownloadClick
                    )
                }
                groupText(R.string.perf_about)
                item {
                    Preference(
                        title = stringResource(id = R.string.pref_app),
                        summary = stringResource(id = R.string.pref_app_summary),
                        leaderIconRes = R.drawable.iconmonstr_github_5
                    ) {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/shizheng233/CopyMangaJava")
                        )
                        context.startActivity(intent)
                    }
                }
                item {
                    Preference(
                        title = stringResource(id = R.string.about),
                        summary = stringResource(id = R.string.pref_about_summary),
                        leaderIconRes = R.drawable.ic_manga_info_main,
                        onClick = onAboutClick,
                    )
                }
            }
        }
    }

}

private fun getFileCacheDir(context: Context): File {
    return (context.externalCacheDirs + context.cacheDir).firstNotNullOfOrNull {
        it.makeDirIfNoExist()
    }.let { file ->
        checkNotNull(file) {
            val dirs =
                (context.externalCacheDirs + context.cacheDir).joinToString(";") {
                    it.absolutePath
                }
            "Cannot find directory for PagesCache: [$dirs]"
        }
    }
}

private suspend inline fun clearCache(
    file: File,
    crossinline onFinished: (String) -> Unit,
) = withContext(Dispatchers.IO) {
    try {
        file.deleteRecursively()
        val size = file.getSize()
        onFinished(size)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun File.getSize(): String {
    return FileCacheUtils.getFormatSize(FileCacheUtils.getFolderSize(this).toDouble())
}



