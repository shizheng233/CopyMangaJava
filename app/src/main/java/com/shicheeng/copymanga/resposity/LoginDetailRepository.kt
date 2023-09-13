package com.shicheeng.copymanga.resposity

import com.shicheeng.copymanga.data.logininfoshort.LoginInfoShortDataModel
import com.shicheeng.copymanga.domin.CopyMangaApi
import com.shicheeng.copymanga.util.UIState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginDetailRepository @Inject constructor(
    private val copyMangaApi: CopyMangaApi,
) {

    fun detail(): Flow<UIState<LoginInfoShortDataModel>> = flow {
        emit(UIState.Loading)
        try {
            val data = copyMangaApi.shortInfo()
            emit(UIState.Success(data))
        } catch (e: Exception) {
            emit(UIState.Error(e))
        }

    }

}