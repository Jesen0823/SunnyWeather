package com.jesen.cod.sunnyweather.logic.network
/**
 * 访问天气API的Retrofit接口
 * **/
import com.jesen.cod.sunnyweather.WeatherApplication
import com.jesen.cod.sunnyweather.logic.model.DailyResponse
import com.jesen.cod.sunnyweather.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {
    @GET("v2.5/${WeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng:String, @Path("lat") lat:String):
            Call<RealtimeResponse>

    @GET("v2.5/${WeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng: String, @Path("lat") lat: String):
            Call<DailyResponse>
}