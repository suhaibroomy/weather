package com.suroid.weatherapp.db

import androidx.room.Dao
import androidx.room.Query
import com.suroid.weatherapp.models.local.CityEntity
import io.reactivex.Single

@Dao
abstract class CityDao : BaseDao<CityEntity> {

    /**
     * Get all cities from the DB.
     */
    @Query("SELECT * FROM city")
    abstract fun getAllCities(): Single<List<CityEntity>>

    /**
     * Search Cities in DB for query
     * @param query query to be searched for
     */
    @Query("SELECT * FROM city WHERE name LIKE :query LIMIT 100")
    abstract fun search(query: String): Single<List<CityEntity>>
}