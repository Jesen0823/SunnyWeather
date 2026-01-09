package com.jesen.cod.sunnyweather.ui.weather

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jesen.cod.sunnyweather.R
import com.jesen.cod.sunnyweather.databinding.ActivityWeatherBinding
import com.jesen.cod.sunnyweather.databinding.ForecastBinding
import com.jesen.cod.sunnyweather.databinding.LifeIndexBinding
import com.jesen.cod.sunnyweather.databinding.NowBinding
import com.jesen.cod.sunnyweather.logic.model.Weather
import com.jesen.cod.sunnyweather.logic.model.getSky

import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityWeatherBinding
    private lateinit var nowBinding: NowBinding
    private lateinit var forecastBinding: ForecastBinding
    private lateinit var lifeIndexBinding: LifeIndexBinding

    val viewModel by lazy { ViewModelProvider(this)[WeatherViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWeatherBinding.inflate(layoutInflater)
        nowBinding = NowBinding.bind(mBinding.nowLayout.root)
        forecastBinding = ForecastBinding.bind(mBinding.forecastLayout.root)
        lifeIndexBinding = LifeIndexBinding.bind(mBinding.lifeIndexLayout.root)

        setStatusBar()
        setContentView(mBinding.root)
        initDrawerLayout()

        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        viewModel.weatherLiveData.observe(this, Observer<Result<Weather>> { result ->
            val weather = result.getOrNull()
            Log.i("WeatherActivity", weather.toString())
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, R.string.get_weather_faild, Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            mBinding.swipeRefresh.isRefreshing = false
        })
        mBinding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        refreshWeather()
        mBinding.swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }

    }

     fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
         mBinding.swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        nowBinding.placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        // 填充now.xml数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        nowBinding.currentTemp.text = currentTempText
        nowBinding.currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        nowBinding.currentAQI.text = currentPM25Text
        nowBinding.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        // 填充forecast.xml布局
        forecastBinding.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(
                R.layout.forecast_item,
                forecastBinding.forecastLayout, false
            )
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            Log.i("WeatherActivity", "AA " + skycon.toString())
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            Log.i("WeatherActivity", skycon.toString())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            forecastBinding.forecastLayout.addView(view)
        }

        // 填充life_index数据
        val lifeIndex = daily.lifeIndex
        lifeIndexBinding.coldRiskText.text = lifeIndex.coldRisk[0].desc
        lifeIndexBinding.dressingText.text = lifeIndex.dressing[0].desc
        lifeIndexBinding.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        lifeIndexBinding.carWashingText.text = lifeIndex.carWashing[0].desc
        mBinding.weatherLayout.visibility = View.VISIBLE
    }

    private fun setStatusBar() {
        // activity布局在状态栏上面
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        // 状态栏透明
        window.statusBarColor = Color.TRANSPARENT
    }

    private fun initDrawerLayout() {
        nowBinding.navBtn.setOnClickListener {
            mBinding.drawerLayout.openDrawer(GravityCompat.START)
        }

        mBinding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                manager.hideSoftInputFromWindow(
                    drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })
    }

    fun closeDrawerLayout() {
        mBinding.drawerLayout.closeDrawers()
    }
}
