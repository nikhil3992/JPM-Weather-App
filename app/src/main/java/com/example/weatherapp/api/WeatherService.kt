package com.example.weatherapp.api

import com.example.weatherapp.data.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("data/2.5/weather")
    suspend fun getWeatherResponse(@Query("q") cityName: String, @Query("appid") appid: String): WeatherResponse

    @GET("data/2.5/weather")
    suspend fun getWeatherResponse(@Query("lat") latitude: Double, @Query("lon") longitude: Double, @Query("appid") appid: String): WeatherResponse
}