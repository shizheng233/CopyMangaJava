package com.shicheeng.copymanga.resposity

import android.util.Base64
import com.shicheeng.copymanga.dao.MangaLoginDao
import com.shicheeng.copymanga.data.login.LocalLoginDataModel
import com.shicheeng.copymanga.data.login.toLoginDataModel
import com.shicheeng.copymanga.domin.CopyMangaApi
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import com.shicheeng.copymanga.util.LoginState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(
    private val copyMangaApi: CopyMangaApi,
    private val loginDao: MangaLoginDao,
    private val settingPref: SettingPref,
) {
    suspend fun login(username: String, password: String) = flow {
        emit(LoginState.Loading)
        val salt = (0..Int.MAX_VALUE).random()
        val passwordEncode = "$password-${salt}".toByteArray(Charset.forName("utf-8"))
        val passwordB64 = Base64.encodeToString(passwordEncode, Base64.DEFAULT)
        try {
            val loginDataModel =
                copyMangaApi.login(username, passwordB64 = passwordB64, salt = salt)
            val loginLocal = loginDataModel.toLoginDataModel(isSelected = true)
            loginDao.updateOrInsertLoginData(loginLocal)
            val newList = loginDao
                .getLoginDataAsync()
                .map { x -> x.copy(selected = x.userID == loginLocal.userID) }
            settingPref.selectedUUId(uuid = loginLocal.userID)
            loginDao.updateOrInsertLoginData(localLoginDataModels = newList.toTypedArray())
            emit(LoginState.Success(loginDataModel))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(LoginState.Error(e))
        }
    }

    fun getAllLoginInstance() = loginDao.getLoginData()

    fun getUserByUUid(uuid: String) = loginDao.getLoginDataByUserId(uuid)

    fun deleteOneInstance(localLoginDataModel: LocalLoginDataModel) = loginDao::deleteLoginData

    suspend fun selectOne(
        uuid: String,
    ) = withContext(Dispatchers.IO) {
        val newList = loginDao.getLoginDataAsync().map { x ->
            x.copy(selected = x.userID == uuid)
        }
        loginDao.updateOrInsertLoginData(localLoginDataModels = newList.toTypedArray())
        settingPref.selectedUUId(uuid)
    }

    fun testLoginStatus(
        uuid: String? = settingPref.loginPerson,
    ) = flow {
        val prevLoginInfo = loginDao.getLoginDataByUserIdSafety(uuid)
        if (prevLoginInfo == null) {
            emit(null)
            return@flow
        }
        try {
            val info = copyMangaApi.loginInfo()
                .toLoginDataModel(
                    localLoginDataModel = prevLoginInfo,
                    isSelected = prevLoginInfo.selected,
                    isExpired = false
                )
            loginDao.updateOrInsertLoginData(info)
            emit(null)
        } catch (e: Exception) {
            val info = prevLoginInfo.copy(isExpired = true)
            loginDao.updateOrInsertLoginData(info)
            emit(e)
        }
    }

}