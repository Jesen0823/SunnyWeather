package com.jesen.cod.sunnyweather.logic
/**
 * 将异步获取的数据以响应式编程方式通知给上一层
 * **/
import androidx.lifecycle.liveData
import com.jesen.cod.sunnyweather.logic.model.Place
import com.jesen.cod.sunnyweather.logic.network.WeatherNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.RuntimeException

object Repository {

    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {
        val result = try {
            val placeResponse = WeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok"){
                val places = placeResponse.places
                Result.success(places)
            }else{
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        }catch (e: Exception){
            Result.failure<List<Place>>(e)
        }
        emit(result)
    }
}