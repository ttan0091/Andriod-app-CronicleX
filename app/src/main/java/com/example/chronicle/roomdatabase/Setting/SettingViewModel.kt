package com.example.chronicle.roomdatabase.Setting
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingViewModel (application: Application) : AndroidViewModel(application) {
    private val cRepository: SettingRepository
    init{
        cRepository = SettingRepository(application)
    }
    val allSettings: LiveData<List<Setting>> = cRepository.allSettings.asLiveData()

    fun insertSetting(setting: Setting) = viewModelScope.launch(Dispatchers.IO) {
        cRepository.insert(setting)
    }

    fun updateSetting(setting: Setting) = viewModelScope.launch(Dispatchers.IO) {
        cRepository.update(setting)

    }

    fun deleteSetting(setting: Setting) = viewModelScope.launch(Dispatchers.IO) {
        cRepository.delete(setting)
    }

    suspend fun getSetting(i: Int): Setting? {
        return cRepository.getSetting(i)
    }
}