package com.jesen.cod.sunnyweather.logic.dao

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.google.gson.Gson
import com.jesen.cod.sunnyweather.WeatherApplication
import com.jesen.cod.sunnyweather.logic.model.Place

/**
 * SharedPreferences相关
 * **/
object PlaceDao {

    fun savePlace(place: Place){
        sharedPreferences().edit{
            Log.i("PlaceDao", Gson().toJson(place))
            putString("place",Gson().toJson(place))
        }
    }

    fun getSavedPlace():Place{
        val placeJson = sharedPreferences().getString("place", "")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")

    private fun sharedPreferences() = WeatherApplication.context.
        getSharedPreferences("WEATHER_APP", Context.MODE_PRIVATE)
}