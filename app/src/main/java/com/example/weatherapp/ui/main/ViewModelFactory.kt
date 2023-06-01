package com.example.weatherapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.api.ApiHelper
import com.example.weatherapp.repository.MainRepository

class ViewModelFactory(val apiHelper: ApiHelper): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(MainRepository(apiHelper)) as T
        }

        throw IllegalArgumentException("Unknown class")
    }
}