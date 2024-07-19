package com.example.chronicle.roomdatabase.Setting

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Setting")
data class Setting(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val isDarkTheme: Boolean,
    val isLocationEnabled: Boolean,
    val language: String="",

    val diaryHistory : String = "empty"
)
