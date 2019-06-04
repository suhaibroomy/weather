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
     * @return [Single] to be subscribed
     */
    fun getAllCities(): Single<List<CityEntity>> {
        return cityDao.getAllCities()
    }

    /**
     * Search city for the matching query from the db
     * @param query [String] string to be searched
     * @return [Single] to be subscribed
     */
    fun searchForCity(query: String): Single<List<CityEntity>> {
        return cityDao.search(query)
    }

    /**
     * Fetch all selected cities from the db
     * @return [Flowable] to be subscribed for. It will automatically emit new values
     */
    fun getSelectedCities(): Flowable<List<CityEntity>> {
        return selectedCityDao.getSelectedCities()
    }

    /**
     * Save city in the db. This should not be called from Main Thread
     * @param cityEntity [CityEntity] to be saved
     */
    @WorkerThread
    fun saveCity(cityEntity: CityEntity) {
        cityDao.upsert(cityEntity)
    }

    /**
     * Save selected city in the db
     * @param cityId [Int] id of the city to be saved
     * @return [Completable] to be subscribed for
     */
    @UiThread
    fun saveSelectedCity(cityId: Int): Completable {
        return Completable.fromAction {
            selectedCityDao.upsert(SelectedCityEntity(cityId))
        }
    }
}