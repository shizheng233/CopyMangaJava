package com.shicheeng.copymanga.fm.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.fm.domain.makeDirIfNoExist
import com.shicheeng.copymanga.fm.reader.ReaderMode
import com.shicheeng.copymanga.util.FileCacheUtils
import kotlinx.coroutines.launch
import java.io.File

class SettingFragment : PreferenceFragmentCompat(), View.OnAttachStateChangeListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val c = super.onCreateView(inflater, container, savedInstanceState)
        c.addOnAttachStateChangeListener(this)
        return c
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.pref_setting, rootKey)

        val cache = getFileCacheDir()
        val exCache = checkNotNull(requireContext().cacheDir)

        findPreference<ListPreference>("pref_orientation_key")?.apply {
            entryValues = arrayOf(
                ReaderMode.NORMAL.name,
                ReaderMode.WEBTOON.name,
                ReaderMode.STANDARD.name
            )
            if (value == null) {
                value = ReaderMode.NORMAL.name
            }
        }

        findPreference<Preference>("key_clear_cache")?.apply {
            val cacheSize = exCache.getSize()
            summary = getString(R.string.cache_used, cacheSize)
            setOnPreferenceClickListener {
                clearCache(it, exCache)
                true
            }
        }

        findPreference<Preference>("key_clear_cache_pager")?.apply {
            val cacheSize = cache.getSize()
            summary = cacheSize
            setOnPreferenceClickListener {
                clearCache(it, cache)
                true
            }
        }

        findPreference<Preference>("key_see_download_manga")?.setOnPreferenceClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToDownloadInfoFragment()
            findNavController().navigate(action)
            true
        }

        findPreference<Preference>("app_project_website")?.setOnPreferenceClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/shizheng233/CopyMangaJava")
            )
            startActivity(intent)
            true
        }

        findPreference<Preference>("app_info")?.setOnPreferenceClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToAboutLibraries()
            findNavController().navigate(action)
            true
        }
    }

    private fun getFileCacheDir(): File {
        return (requireContext().externalCacheDirs + requireContext().cacheDir).firstNotNullOfOrNull {
            it.makeDirIfNoExist()
        }.let { file ->
            checkNotNull(file) {
                val dirs =
                    (requireContext().externalCacheDirs + requireContext().cacheDir).joinToString(";") {
                        it.absolutePath
                    }
                "Cannot find directory for PagesCache: [$dirs]"
            }
        }
    }

    private fun clearCache(preference: Preference, file: File) {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            try {
                preference.isEnabled = false
                file.deleteRecursively()
                val size = file.getSize()
                preference.summary = size
            } catch (e: Exception) {
                preference.summary = e.message
            } finally {
                preference.isEnabled = true
            }
        }

    }

    private fun File.getSize(): String {
        return FileCacheUtils.getFormatSize(FileCacheUtils.getFolderSize(this).toDouble())
    }

    override fun onViewAttachedToWindow(v: View) {
        val insetsCompat = ViewCompat.getRootWindowInsets(v)
        val systemBarInsets = insetsCompat?.getInsets(WindowInsetsCompat.Type.systemBars())
        v.updatePadding(bottom = systemBarInsets?.bottom ?: 0)
    }

    override fun onViewDetachedFromWindow(v: View) {

    }

}
