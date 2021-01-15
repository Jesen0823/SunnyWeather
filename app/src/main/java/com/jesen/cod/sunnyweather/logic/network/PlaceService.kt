package com.jesen.cod.sunnyweather.logic.network
/**
 * 搜索城市接口
 * **/
import com.jesen.cod.sunnyweather.WeatherApplication
import com.jesen.cod.sunnyweather.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {
    // 参数query接口中表示地名
    @GET("v2/place?token=${WeatherApplication.TOKEN}&lang=zh_CN")
    fun searchPlaces(@Query("query") query:String): Call<PlaceResponse>
}