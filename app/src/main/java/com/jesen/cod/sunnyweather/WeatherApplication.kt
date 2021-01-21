package com.jesen.cod.sunnyweather

import android.app.Application
import android.content.Context

class WeatherApplication : Application(){
    companion object {
        lateinit var context: Context
        const val TOKEN = "cM8HddCKF2Ns1Ccm"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}