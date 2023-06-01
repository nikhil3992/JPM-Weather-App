package com.example.weatherapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.weatherapp.repository.MainRepository
import com.example.weatherapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun getWeather(cityName: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(mainRepository.getWeatherResponse(cityName)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Something went wrong. Please try again!!"))
        }
    }

    fun getWeather(latitude: Double, longitude: Double) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(mainRepository.getWeatherResponse(latitude, longitude)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Something went wrong. Please try again!!"))
        }

    }
}