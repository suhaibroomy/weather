package com.suroid.weatherapp.models

data class WeatherModel(
        var title: String? = null,
        var description: String? = null,
        var temperature: TemperatureModel? = null,
        var windSpeed: Float? = null,
        var humidity: Int? = null,
        var weather_id: Int? = null)