package com.suroid.weatherapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.suroid.weatherapp.models.local.CityEntity
import com.suroid.weatherapp.models.local.CityWeatherEntity
import com.suroid.weatherapp.models.local.SelectedCityEntity

/**
 * The Room database that contains the Data table
 */
@Database(entities = [CityEntity::class, CityWeatherEntity::class, SelectedCityEntity::class], version = 1, exportSchema = false)
@TypeConverters(WeatherModelConverter::class, TemperatureModelConverter::class)
abstract class WeatherDb : RoomDatabase() {

    abstract fun cityDao(): CityDao

    abstract fun cityWeatherDao(): CityWeatherDao

    abstract fun selectedCityDao(): SelectedCityDao

}