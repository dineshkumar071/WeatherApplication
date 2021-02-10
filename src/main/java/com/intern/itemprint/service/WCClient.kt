package com.intern.itemprint.service

import com.intern.itemprint.Repository.model.WCExample
import retrofit2.Call
import retrofit2.http.GET

interface WCClient {
    @GET("data/2.5/forecast/daily?q=london&cnt=7&appid=82a3c576ed39bfde1c054ddb73f4cec0")
    fun getClimate(): Call<WCExample>
}