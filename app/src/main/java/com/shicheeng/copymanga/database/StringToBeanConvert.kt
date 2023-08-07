package com.shicheeng.copymanga.database

import androidx.room.TypeConverter
import com.shicheeng.copymanga.data.MangaSortBean


class StringToBeanConvert {

    companion object {
        private const val SPLIT_OUT = "，"
        private const val SPLIT_INNER = "-"
    }

    @TypeConverter
    fun stringToListBean(string: String): List<MangaSortBean> {
        return buildList {
            string.split(SPLIT_OUT).forEach {
                val inner = it.split(SPLIT_INNER)
                if (inner.size == 1) {
                    add(MangaSortBean(inner[0], inner[0]))
                } else {
                    add(MangaSortBean(inner[0], inner[1]))
                }
            }
        }
    }

    @TypeConverter
    fun listBeanToString(list: List<MangaSortBean>): String {
        return buildString {
            list.forEachIndexed { index, sortBean ->
                append("${sortBean.pathName}$SPLIT_INNER${sortBean.pathWord}")
                if (index != list.lastIndex) {
                    append(SPLIT_OUT)
                }
            }
        }
    }

}