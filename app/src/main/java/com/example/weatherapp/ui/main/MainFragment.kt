package com.example.weatherapp.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.api.ApiHelper
import com.example.weatherapp.api.RetrofitBuilder
import com.example.weatherapp.data.WeatherInfo
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.databinding.FragmentMainBinding
import com.example.weatherapp.utils.Resource
import com.example.weatherapp.utils.Status

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupListeners()
        updateLastSearchedCity()
        checkPermissions()
    }

    private fun updateLastSearchedCity() {
        binding.cityNameEditText.setText(getLastSearchedCity())
    }

    @SuppressLint("MissingPermission")
    private fun getLatLong() {
        val locationManager: LocationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location: Location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) as Location
        val latitude = location.latitude
        val longitude = location.longitude
        fetchWeather("", latitude = latitude, longitude = longitude)

    }

    private fun setupListeners() {
        binding.searchButton.setOnClickListener {
            val cityName = binding.cityNameEditText.text.toString()
            saveLastSearchedCity(cityName)
            fetchWeather(cityName, 0.0,0.0)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory(ApiHelper(RetrofitBuilder.weatherService)))[MainViewModel::class.java]
    }

    private fun fetchWeather(cityName: String, latitude: Double, longitude: Double) {
        val liveData: LiveData<Resource<WeatherResponse>> = if (cityName.isNotEmpty()) {
            viewModel.getWeather(cityName)
        } else {
            viewModel.getWeather(latitude, longitude)
        }
        liveData.observe(viewLifecycleOwner) {
            it?.let { weatherResponseResource ->
                when (weatherResponseResource.status) {
                    Status.LOADING -> {
                        showLoadingUI()
                    }
                    Status.SUCCESS -> {
                       showSuccessUI(weatherResponseResource)
                    }
                    Status.ERROR -> {
                        showErrorUI()
                    }
                }
            }
        }
    }

    // UI Updates
    private fun showLoadingUI() {
        binding.progressBar.visibility = View.VISIBLE
        binding.mainText.visibility = View.GONE
        binding.icon.visibility = View.GONE
        binding.description.visibility = View.GONE
        binding.cityName.visibility = View.GONE
        binding.errorTextView.visibility = View.GONE
    }

    private fun showSuccessUI(weatherResponseResource: Resource<WeatherResponse>) {
        val data: WeatherResponse = weatherResponseResource.data!!
        val weatherInfo: WeatherInfo = data.weather[0]
        binding.progressBar.visibility = View.GONE

        // Due to time constraints, only cityName, description and main text is being displayed. If time permits, I'd show more information to the user
        binding.mainText.visibility = View.VISIBLE
        binding.icon.visibility = View.VISIBLE
        binding.description.visibility = View.VISIBLE
        binding.cityName.visibility = View.VISIBLE
        binding.errorTextView.visibility = View.GONE

        view?.let {
                it1 -> Glide.with(it1)
            // Icons can be saved on the device as drawables instead of fetching from the API. Due to time constraints, I'm fetching it here
            .load("https://openweathermap.org/img/wn/" + (weatherResponseResource.data.weather[0].icon) + ".png")
            .into(binding.icon)
        }
        binding.mainText.text = weatherInfo.main
        binding.cityName.text = data.name
        binding.description.text = weatherInfo.description
    }

    private fun showErrorUI() {
        binding.progressBar.visibility = View.GONE
        binding.mainText.visibility = View.GONE
        binding.icon.visibility = View.GONE
        binding.description.visibility = View.GONE
        binding.cityName.visibility = View.GONE
        binding.errorTextView.visibility = View.VISIBLE
        binding.errorTextView.text = getString(R.string.error_text)
    }

    // Location Permission methods
    private fun checkPermissions() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                getLatLong()
            }
            else -> {
                requestPermissionLauncher.launch(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getLatLong()
            } else {
                // If time permits, I'd show a dialog to the user here to explain why its important to grant location permission
            }
        }

    // Shared Preferences
    private fun saveLastSearchedCity(cityName: String) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(getString(R.string.last_searched_city), cityName)
            apply()
        }
    }

    private fun getLastSearchedCity(): String {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return ""
        return sharedPref.getString(getString(R.string.last_searched_city), "") ?: ""
    }



}