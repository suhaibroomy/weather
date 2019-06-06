package com.suroid.weatherapp.repo

import com.suroid.weatherapp.db.CityDao
import com.suroid.weatherapp.db.SelectedCityDao
import com.suroid.weatherapp.models.local.SelectedCityEntity
import com.suroid.weatherapp.util.RxImmediateSchedulerRule
import com.suroid.weatherapp.util.createCityEntity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class CityEntityRepositoryTest {
    private val cityDao = Mockito.mock(CityDao::class.java)
    private val selectedCityDao = Mockito.mock(SelectedCityDao::class.java)

    private val repo = CityRepository(cityDao, selectedCityDao)

    @Rule
    @JvmField
    val schedulers = RxImmediateSchedulerRule()

    @Test
    fun loadCitiesTest() {
        repo.getAllCities()
        Mockito.verify(cityDao).getAllCities()
    }

    @Test
    fun searchForCityTest() {
        val query = "testQuery"
        repo.searchForCity(query)
        Mockito.verify(cityDao).search(query)
    }

    @Test
    fun getSelectedCitiesTest() {
        repo.getSelectedCities()
        Mockito.verify(selectedCityDao).getSelectedCities()
    }

    @Test
    fun saveCityTest() {
        val cityEntity = createCityEntity()
        repo.saveCity(cityEntity)
        Mockito.verify(cityDao).upsert(cityEntity)
    }

    @Test
    fun saveSelectedCityTest() {
        val cityId = 123
        repo.saveSelectedCity(cityId)
                .test()
                .assertComplete()
                .dispose()

        Mockito.verify(selectedCityDao).upsert(SelectedCityEntity(cityId))
    }
}