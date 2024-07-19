package com.example.chronicle.roomdatabase.Setting

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingDAO {
    @Query("SELECT * FROM Setting")
    fun getAllSettings(): Flow<List<Setting>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: Setting)
    @Update
    suspend fun updateSetting(setting: Setting)
    @Delete
    suspend fun deleteSetting(setting: Setting)

    @Query("SELECT * FROM Setting WHERE id = :id LIMIT 1")
    suspend fun getSetting(id: Int): Setting?
}