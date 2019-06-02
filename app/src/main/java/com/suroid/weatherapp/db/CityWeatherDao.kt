package com.suroid.weatherapp.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.suroid.weatherapp.models.CityWeatherEntity
import io.reactivex.Single


@Dao
abstract class CityWeatherDao : BaseDao<CityWeatherEntity> {

    /**
     * Get all CityWeathers along with CityEntity and forecast.
     */
    @Query("SELECT * FROM city_weather WHERE city_id = :id")
    abstract fun getCityWeatherByCityId(id: Int): Single<CityWeatherEntity>
}