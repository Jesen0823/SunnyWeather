package com.jesen.cod.sunnyweather.logic
/**
 * 将异步获取的数据以响应式编程方式通知给上一层
 * **/
import android.content.Context
import android.util.Log
import androidx.lifecycle.liveData
import com.jesen.cod.sunnyweather.logic.model.Place
import com.jesen.cod.sunnyweather.logic.model.Weather
import com.jesen.cod.sunnyweather.logic.network.WeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

object Repository {

    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
            val placeResponse = WeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok"){
                val places = placeResponse.places
                Result.success(places)
            }else{
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
    }

    fun refreshWeather(lng:String, lat:String) = fire(Dispatchers.IO) {
        Log.i("Repository","${lat} , ${lng}" )

        // 开辟协程
            coroutineScope{
                val deferredRealtime = async {
                    WeatherNetwork.getRealtimeWeather(lng,lat)
                }
                val deferredDaily = async {
                    WeatherNetwork.getDailyWeather(lng,lat)
                }
                val realtimeResponse = deferredRealtime.await()
                val dailyResponse = deferredDaily.await()
                if (realtimeResponse.status == "ok" && dailyResponse.status == "ok"){
                    val weather = Weather(realtimeResponse.result.realtime,
                        dailyResponse.result.daily)
                    Result.success(weather)
                }else{
                    Result.failure(
                        RuntimeException("realtime response status is ${realtimeResponse.status}," +
                                "daily response status is ${dailyResponse.status}")
                    )
                }
            }
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            }catch (e:Exception){
                Result.failure<T>(e)
            }
            emit(result)
        }
}