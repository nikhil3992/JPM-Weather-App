package com.example.weatherapp.api

import com.example.weatherapp.BuildConfig

class ApiHelper(val weatherService: WeatherService) {

    suspend fun getWeatherResponse(cityName: String) = weatherService.getWeatherResponse(cityName, BuildConfig.APP_ID)
    suspend fun getWeatherResponse(latitude: Double, longitude: Double) = weatherService.getWeatherResponse(latitude, longitude, BuildConfig.APP_ID)
}