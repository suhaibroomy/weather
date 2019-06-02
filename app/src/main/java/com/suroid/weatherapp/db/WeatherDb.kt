package com.suroid.weatherapp.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.suroid.weatherapp.models.CityEntity
import com.suroid.weatherapp.models.CityWeatherEntity
import com.suroid.weatherapp.models.SelectedCityEntity

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