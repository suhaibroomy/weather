package com.suroid.weatherapp.db

import androidx.room.Dao
import androidx.room.Query
import com.suroid.weatherapp.models.CityEntity
import com.suroid.weatherapp.models.SelectedCityEntity
import io.reactivex.Flowable

@Dao
abstract class SelectedCityDao : BaseDao<SelectedCityEntity> {

    /**
     * Get all selected cities
     */
    @Query("SELECT c.id, c.country, c.name FROM selected_city sc INNER JOIN city c ON sc.city_id = c.id")
    abstract fun getSelectedCities(): Flowable<List<CityEntity>>
}