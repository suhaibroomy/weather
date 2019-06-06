package com.suroid.weatherapp.util

import com.suroid.weatherapp.models.local.CityEntity
import com.suroid.weatherapp.models.local.CityWeatherEntity
import com.suroid.weatherapp.models.local.TemperatureModel
import com.suroid.weatherapp.models.local.WeatherModel
import com.suroid.weatherapp.models.remote.*

fun createWeatherResponseModel(id: Int): WeatherResponseModel {
    val main = Main(temp = 1.0f,
            humidity = 2,
            temp_max = 3.0f,
            temp_min = 4.0f
    )
    val weather = Weather(id = 1,
            main = "main",
            description = "description",
            icon = "icon")
    val wind = Wind(speed = 1.0f)
    val sys = Sys(country = "country")
    return WeatherResponseModel(main = main,
            weather = ArrayList<Weather>().apply { add(weather) },
            wind = wind,
            dt = 0,
            id = id,
            name = "name",
            sys = sys)
}

fun createWeatherResponseModel() = createWeatherResponseModel(123)

fun createCityEntity() = CityEntity(name = "name", country = "country", id = 123)

fun createCityWeather(date: Long = 0) = CityWeatherEntity(123,
        WeatherModel("title", "description", TemperatureModel(1.1f, 2.2f, 3.3f), 4f, 5, 6),
        date = date)