package com.shicheeng.copymanga.resposity

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.shicheeng.copymanga.data.rank.Item
import com.shicheeng.copymanga.domin.CopyMangaApi
import com.shicheeng.copymanga.pagingsource.RankPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaRankRepository @Inject constructor(
    private val copyMangaApi: CopyMangaApi,
) {

    fun fetchMangaRank(type: String): Flow<PagingData<Item>> {
        return Pager(
            config = PagingConfig(pageSize = 21),
            pagingSourceFactory = {
                RankPagingSource(copyMangaApi, type)
            }
        ).flow
    }

}