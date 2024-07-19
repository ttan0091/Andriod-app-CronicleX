package com.example.chronicle.roomdatabase.Setting

import android.app.Application
import kotlinx.coroutines.flow.Flow

class SettingRepository (application: Application) {
    private var settingDao: SettingDAO =
        SettingDatabase.getDatabase(application).settingDAO()
    val allSettings: Flow<List<Setting>> = settingDao.getAllSettings()
    suspend fun insert(setting: Setting) {
        settingDao.insertSetting(setting)
    }
    suspend fun delete(setting: Setting) {
        settingDao.deleteSetting(setting)
    }
    suspend fun update(setting: Setting) {
        settingDao.updateSetting(setting)
    }
    suspend fun getSetting(id: Int): Setting? {
        return settingDao.getSetting(id)
    }
}