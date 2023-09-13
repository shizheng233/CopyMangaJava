package com.shicheeng.copymanga.resposity

import com.shicheeng.copymanga.dao.MangaLoginDao
import com.shicheeng.copymanga.ui.screen.setting.SettingPref
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 防止依赖注入循环。
 */
@Singleton
class LoginTokenRepository @Inject constructor(
    private val loginDao: MangaLoginDao,
    private val settingPref: SettingPref,
) {

    /**
     * 登录Token，没有则返回null。
     */
    val token: String?
        get() {
            return loginDao.getCurrentToken(settingPref.loginPerson ?: return null)
        }

    val isExpired: Boolean
        get() {
            return loginDao.isExpired(settingPref.loginPerson ?: return true)
        }

}