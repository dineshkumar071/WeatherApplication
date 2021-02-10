package com.intern.itemprint.retrofit

import com.google.gson.GsonBuilder
import com.intern.itemprint.Repository.WCRepository
import com.intern.itemprint.service.WCClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WCRetrofit {
    val gson = GsonBuilder()
        .setLenient()
        .create()
    val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    val okHttp = OkHttpClient.Builder().addInterceptor(logger)
    fun retrofit(): WCClient? {

        return Retrofit.Builder()
            .baseUrl(WCRepository.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)).client(okHttp.build())
            .build().create(WCClient::class.java)
    }
}