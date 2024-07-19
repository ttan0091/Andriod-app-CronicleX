package com.example.chronicle.data

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * event class used to storing the information in form of object and facilitate deserialization
 * **/
data class Event(
    val eventId: String? = null,
    val userId: String? = null,
    val title: String,
    val body: String,
    val date: String,
    val time: String,
    val images: List<String?> = listOf(),
    val location: String = "",
    val weather: String = "",
    @field:JvmField     // Required for isPublic or it will always return the default value
    val isPublic: Boolean = false,
    val tag: String = "") {
    // Add a no-argument constructor for deserialize object
    constructor() : this(null, null,"", "", "", "", listOf(), "", "", false, "")

    /**
     * method to get the event date in fixed format
     * **/
    @RequiresApi(Build.VERSION_CODES.O)
    fun getEventDate(): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.parse(date, formatter)
    }
}
