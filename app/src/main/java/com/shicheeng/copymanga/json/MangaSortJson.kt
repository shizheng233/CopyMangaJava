package com.shicheeng.copymanga.json

import com.shicheeng.copymanga.data.MangaSortBean

enum class MangaSortJson {

    ORDER, THEME, PATH;

    companion object {
        @JvmStatic
        val order: List<MangaSortBean>
            get() {
                val list: MutableList<MangaSortBean> = ArrayList()
                val bean1 = MangaSortBean()
                bean1.pathName = "最久更新"
                bean1.pathWord = "datetime_updated"
                list.add(bean1)
                val dateUpdateNearly = MangaSortBean()
                dateUpdateNearly.pathName = "最近更新"
                dateUpdateNearly.pathWord = "-datetime_updated"
                list.add(dateUpdateNearly)
                val bean2 = MangaSortBean()
                bean2.pathName = "最热"
                bean2.pathWord = "-popular"
                list.add(bean2)
                val unpopular = MangaSortBean()
                unpopular.pathName = "最冷"
                unpopular.pathWord = "popular"
                list.add(unpopular)
                return list
            }

        @JvmStatic
        val topPath = listOf(
            MangaSortBean("无", ""),
            MangaSortBean("日本", "japan"),
            MangaSortBean("已完结", "finish"),
            MangaSortBean("韩国", "korea"),
            MangaSortBean("欧美", "west"),
        )
    }
}