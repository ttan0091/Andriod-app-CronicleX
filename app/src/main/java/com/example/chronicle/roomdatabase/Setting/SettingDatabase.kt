package com.example.chronicle.roomdatabase.Setting

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Setting::class], version = 1, exportSchema = false)
abstract class SettingDatabase : RoomDatabase() {
    abstract fun settingDAO(): SettingDAO
    companion object {
        @Volatile
        private var INSTANCE: SettingDatabase? = null
        fun getDatabase(context: Context): SettingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SettingDatabase::class.java,
                    "setting_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
