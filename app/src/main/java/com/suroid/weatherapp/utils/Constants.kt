package com.suroid.weatherapp.utils

import java.lang.Error
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
const val CITIES_JSON_FILE_NAME = "cities.json"
var WEATHER_EXPIRY_THRESHOLD_TIME = TimeUnit.MINUTES.toSeconds(30)

sealed class Errors(message: String): Error(message) {
    class PermissionError(message: String): Errors(message)
}