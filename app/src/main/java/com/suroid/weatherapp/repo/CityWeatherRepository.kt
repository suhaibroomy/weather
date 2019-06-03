package com.suroid.weatherapp.repo

import com.suroid.weatherapp.api.WeatherApi
import com.suroid.weatherapp.db.CityWeatherDao
import com.suroid.weatherapp.models.CityEntity
import com.suroid.weatherapp.models.CityWeatherEntity
import com.suroid.weatherapp.utils.extensions.mapToCityEntity
import com.suroid.weatherapp.utils.extensions.mapToWeatherEntity
import io.reactivex.Single

/**
 * CityWeatherRepository handles all the data related task, it can act as a mediator between data accessor and data provider modules
 * @param cityWeatherDao [CityWeatherDao] instance to load local data from
 * @param weatherApi [WeatherApi] instance to load remote data from
 */
class CityWeatherRepository(private val cityRepo: CityRepository, private val cityWeatherDao: CityWeatherDao, private val weatherApi: WeatherApi) {


    /**
     * Fetch city weather by id from the db
     * @param cityEntity [CityEntity] to fetched weather for
     */
    fun getCityWeatherByCityId(cityEntity: CityEntity): Single<CityWeatherEntity> {
        return cityWeatherDao.getCityWeatherByCityId(cityEntity.id)
    }

    /**
     * Fetch city weather for provided city from api and save/update it in the db
     * @param cityEntity [CityEntity] city to be fetched weather for
     * @return [Single] to subscribe
     */
    fun fetchWeatherOfCity(cityEntity: CityEntity): Single<CityWeatherEntity> {
        return weatherApi.getWeatherWithId(cityEntity.id)
                .map {
                    it.mapToWeatherEntity(cityEntity)
                }
                .doOnSuccess {
                    cityWeatherDao.update(it)
                }
    }

    /**
     * Fetch city weather for provided location and save it in the db
     * @param lat [Double] latitude of the location
     * @param long [Double] Longitude of the location
     */
    fun fetchWeatherWithLatLong(lat: Double, long: Double): Single<CityWeatherEntity> {
        return weatherApi.getWeatherWithLatLong(lat, long)
                .doOnSuccess {
                    cityRepo.saveCity(cityEntity = it.mapToCityEntity())
                }
                .map {
                    it.mapToWeatherEntity()
                }
                .doAfterSuccess {
                    cityWeatherDao.upsert(it)
                }

    }
}