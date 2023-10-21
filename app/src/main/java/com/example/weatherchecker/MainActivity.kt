package com.example.weatherchecker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.weatherchecker.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val OPEN_WEATHER_MAP_BASE_URL = "https://api.openweathermap.org/data/2.5/"
const val OPEN_WEATHER_MAP_API_KEY = "f89e8871f59604592ec9a491b8d70802"

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var searchInput : EditText
    private lateinit var searchIconn : ImageView
    private lateinit var weatherImage : ImageView
    private lateinit var temperatureText : TextView
    private lateinit var locationText : TextView
    private lateinit var weatherConditionText : TextView
    private lateinit var city : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchInput = binding.edtLocation
        searchIconn = binding.searchIcon
        city = "Bhagalpur"

        fetchCityWeatherDetails()

        searchIconn.setOnClickListener {
            hideKeyboard()
            city = searchInput.text.toString()
            searchInput.text.clear()
            fetchCityWeatherDetails()
        }
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(searchInput.windowToken, 0)
    }

    private fun fetchCityWeatherDetails() {
        weatherImage = binding.weatherImage
        temperatureText = binding.txtTemprature
        locationText = binding.txtLocation
        weatherConditionText = binding.txtCondition

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(OPEN_WEATHER_MAP_BASE_URL)
            .build()
            .create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(city, OPEN_WEATHER_MAP_API_KEY)
        response.enqueue(object : Callback<WeatherCity> {
            override fun onResponse(call: Call<WeatherCity>, response: Response<WeatherCity>) {
                val responeBody = response.body()

                if(response.isSuccessful && responeBody != null) {
                    var temp = responeBody.main.temp.toInt() - 273
                    temperatureText.text = "${temp} °C"
                    locationText.text = responeBody.name
                    weatherConditionText.text = responeBody.weather[0].main

                    setWeatherImage(responeBody.weather[0].main)
                }
            }

            override fun onFailure(call: Call<WeatherCity>, t: Throwable) {
            }
        })
    }

    private fun setWeatherImage(weatherCondition : String) {
        val drawableId = when(weatherCondition.toLowerCase()) {
            "sunny" -> R.drawable.sunny
            "haze" -> R.drawable.hazy
            "clouds" -> R.drawable.cloudy
            "rain" -> R.drawable.rainy
            "thunder" -> R.drawable.thunder
            "clear" -> R.drawable.clear
            else -> R.drawable.noimage
        }

        weatherImage.setImageResource(drawableId)
    }
}