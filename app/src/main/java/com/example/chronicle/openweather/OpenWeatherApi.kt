package com.example.chronicle.openweather

import com.google.gson.annotations.SerializedName
data class OpenWeatherApi (

    @SerializedName("lat"             ) var lat            : Double?             = null,
    @SerializedName("lon"             ) var lon            : Double?             = null,
    @SerializedName("timezone"        ) var timezone       : String?             = null,
    @SerializedName("timezone_offset" ) var timezoneOffset : Int?                = null,
    @SerializedName("current"         ) var current        : Current?            = Current(),
    @SerializedName("minutely"        ) var minutely       : ArrayList<Minutely> = arrayListOf(),
    @SerializedName("hourly"          ) var hourly         : ArrayList<Hourly>   = arrayListOf(),
    @SerializedName("daily"           ) var daily          : ArrayList<Daily>    = arrayListOf()

)