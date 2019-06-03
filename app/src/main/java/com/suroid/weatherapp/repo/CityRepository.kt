package com.suroid.weatherapp.repo

import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import com.suroid.weatherapp.db.CityDao
import com.suroid.weatherapp.db.SelectedCityDao
import com.suroid.weatherapp.models.CityEntity
import com.suroid.weatherapp.models.SelectedCityEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Singleton

/**
 * CityRepository handles all the data related task, it can act as a mediator between data accessor and data provider modules
 * @param cityDao [CityDao] instance to load data from
 */
@Singleton
class CityRepository(private val cityDao: CityDao, private val selectedCityDao: SelectedCityDao) {

    /**
     * Load All cities from database
     */
    fun getAllCities(): Single<List<CityEntity>> {
        return cityDao.getAllCities()
    }

    fun searchForCity(query: String): Single<List<CityEntity>> {
        return cityDao.search(query)
    }

    fun getSelectedCities(): Flowable<List<CityEntity>> {
        return selectedCityDao.getSelectedCities()
    }

    @WorkerThread
    fun saveCity(cityEntity: CityEntity) {
        cityDao.upsert(cityEntity)
    }

    @UiThread
    fun saveSelectedCity(cityId: Int): Completable {
        return Completable.fromAction {
            selectedCityDao.upsert(SelectedCityEntity(cityId))
        }
    }
}