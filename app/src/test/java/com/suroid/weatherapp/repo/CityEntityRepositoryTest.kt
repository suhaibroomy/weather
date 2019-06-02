package com.suroid.weatherapp.repo

import com.suroid.weatherapp.db.CityDao
import com.suroid.weatherapp.db.SelectedCityDao
import com.suroid.weatherapp.util.RxImmediateSchedulerRule
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
}