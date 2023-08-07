package com.shicheeng.copymanga.fm.reader

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.shicheeng.copymanga.R
import com.shicheeng.copymanga.fm.reader.noraml.ReaderPageFragment
import com.shicheeng.copymanga.fm.reader.standard.ReaderPagerStandardFragment
import com.shicheeng.copymanga.fm.reader.webtoon.WebtoonReaderFragment
import java.util.EnumMap

class ReaderManager(
    private val supportFragmentManager: FragmentManager,
    @IdRes private val containerId: Int,
) {

    private val modeMap = EnumMap<ReaderMode, Class<out BaseReader<*>>>(ReaderMode::class.java)

    init {
        modeMap[ReaderMode.NORMAL] = ReaderPageFragment::class.java
        modeMap[ReaderMode.WEBTOON] = WebtoonReaderFragment::class.java
        modeMap[ReaderMode.STANDARD] = ReaderPagerStandardFragment::class.java
    }

    val currentReader: BaseReader<*>?
        get() = supportFragmentManager.findFragmentById(containerId) as? BaseReader<*>

    val currentReaderMode: ReaderMode?
        get() {
            val readerClass = currentReader?.javaClass ?: return null
            return modeMap.entries.find { it.value == readerClass }?.key
        }

    fun replace(newMode: ReaderMode) {
        val readerClass = requireNotNull(modeMap[newMode])
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(containerId, readerClass, null, null)
        }
    }

    fun replace(reader: BaseReader<*>) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(containerId, reader)
        }
    }

}

enum class ReaderMode(@IdRes val id: Int) {
    NORMAL(R.string.japanese_r_to_l),
    WEBTOON(R.string.korea_chinese_top_to_bottom),
    STANDARD(R.string.manga_mode_l_t_r);

    companion object {
        fun idOf(id: Int?) = values().firstOrNull {
            it.id == id
        }
    }

}