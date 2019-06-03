package com.suroid.weatherapp.utils.extensions

import com.suroid.weatherapp.models.CityEntity
import com.suroid.weatherapp.models.CityWeatherEntity
import com.suroid.weatherapp.models.TemperatureModel
import com.suroid.weatherapp.models.WeatherModel
import com.suroid.weatherapp.models.remote.WeatherResponseModel
import com.suroid.weatherapp.utils.currentTimeInSeconds


/**
 * Extension function WeatherResponseModel to CityWeatherEntity
 * */
fun WeatherResponseModel.mapToWeatherEntity(cityEntity: CityEntity? = null): CityWeatherEntity {
    val temperatureModel = TemperatureModel(main.temp, main.temp_min, main.temp_max)
    val weatherModel = WeatherModel(getWeather()?.main, getWeather()?.description, temperatureModel, wind.speed, main.humidity, getWeather()?.id)
    return CityWeatherEntity(cityEntity?.id ?: id, weatherModel, date = currentTimeInSeconds())
}

/**
 * Extension function WeatherResponseModel to CityWeatherEntity
 * */
fun WeatherResponseModel.mapToCityEntity(): CityEntity {
    return CityEntity(name, sys.country, id)
}