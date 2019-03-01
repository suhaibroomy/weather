package com.suroid.weatherapp.utils

import java.util.concurrent.TimeUnit

const val BASE_URL = "http://api.openweathermap.org/data/2.5/"
const val CITIES_JSON_FILE_NAME = "cities.json"
var WEATHER_EXPIRY_THRESHOLD_TIME = TimeUnit.MINUTES.toSeconds(30)

const val CITY_MODEL = "city_model"
