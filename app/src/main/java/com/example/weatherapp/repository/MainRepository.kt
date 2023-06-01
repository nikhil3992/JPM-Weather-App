package com.example.weatherapp.repository

import com.example.weatherapp.api.ApiHelper

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun getWeatherResponse(cityName: String) = apiHelper.getWeatherResponse(cityName)
    suspend fun getWeatherResponse(latitude: Double, longitude: Double) = apiHelper.getWeatherResponse(latitude, longitude)
}