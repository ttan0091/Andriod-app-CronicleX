package com.example.chronicle.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * the api client used for calling OpenAI api for chatbot feature
 * **/
object ApiClient {

    @Volatile
    private var INSTANCE: ApiInterface? = null

    fun getInstance(): ApiInterface {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface::class.java)
                .also {
                    INSTANCE = it
                }
        }
    }
}