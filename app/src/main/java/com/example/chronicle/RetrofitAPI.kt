package com.example.chronicle

import com.example.chronicle.openweather.OpenWeatherApi
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitAPI {
    @GET("onecall")
    fun getData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): Call<OpenWeatherApi?>
}