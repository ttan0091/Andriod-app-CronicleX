package com.example.chronicle.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

/**
    Manage the date into format of e.g. "Mon 12th March, 2024"
 **/
@RequiresApi(Build.VERSION_CODES.O)
fun formatSelectedDate(date: LocalDate): String {
    val day = date.dayOfWeek.toString().substring(0, 3).lowercase().titlecaseFirstCharIfItIsLowercase()
    val month = date.month.toString().lowercase().titlecaseFirstCharIfItIsLowercase()
    val dayOfMonth = date.dayOfMonth
    val daySuffix = getSuffix(dayOfMonth)

    return "${day} $dayOfMonth$daySuffix $month, ${date.year}"
}

fun getSuffix(day: Int): String {
    return when (day) {
        1, 21, 32 -> "st"
        2, 22 -> "st"
        3, 23 -> "rd"
        else -> "th"
    }
}