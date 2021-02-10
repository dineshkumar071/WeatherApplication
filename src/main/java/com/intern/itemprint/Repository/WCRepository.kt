package com.intern.itemprint.Repository

import com.google.gson.GsonBuilder
import com.intern.itemprint.Repository.model.WCExample
import com.intern.itemprint.common.WCApplication.Companion.instance
import com.intern.itemprint.retrofit.WCRetrofit

object WCRepository {
    var BASE_URL: String = "https://api.openweathermap.org/"
    fun getClimate()=WCRetrofit.retrofit()?.getClimate()

    fun saveUserInSharedPreference(climate:WCExample?) {
        val userGson = GsonBuilder().create()
        val city: String? = userGson.toJson(climate)
        instance?.getPrefer()?.climate = city
    }

    fun retrieveUserFromSharedPreference(): String? = instance?.getPrefer()?.climate

}