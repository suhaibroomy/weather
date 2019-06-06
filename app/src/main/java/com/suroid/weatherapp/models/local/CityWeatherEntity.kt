package com.suroid.weatherapp.models.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "city_weather",
        foreignKeys = [ForeignKey(entity = CityEntity::class, parentColumns = ["id"], childColumns = ["city_id"], onDelete = ForeignKey.CASCADE)])
data class CityWeatherEntity(@PrimaryKey
                             @ColumnInfo(name = "city_id")
                             val cityId: Int,
                             @ColumnInfo(name = "current_weather")
                             val currentWeather: WeatherModel?,
                             val date: Long = 0,
                             @ColumnInfo(name = "daily_forecast")
                             val dailyForecast: List<TemperatureModel> = ArrayList(),
                             @ColumnInfo(name = "hourly_forecast")
                             val hourlyForecast: List<TemperatureModel> = ArrayList()) {


    override fun equals(other: Any?): Boolean {
        return other is CityWeatherEntity && other.cityId == cityId
    }

    override fun hashCode(): Int {
        return cityId.hashCode()
    }
}
