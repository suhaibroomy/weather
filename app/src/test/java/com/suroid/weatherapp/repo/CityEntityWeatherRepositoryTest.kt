package com.suroid.weatherapp.repo

import com.suroid.weatherapp.api.WeatherApi
import com.suroid.weatherapp.db.CityWeatherDao
import com.suroid.weatherapp.util.*
import com.suroid.weatherapp.utils.currentTimeInSeconds
import com.suroid.weatherapp.utils.extensions.mapToCityEntity
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.never

@RunWith(JUnit4::class)
class CityEntityWeatherRepositoryTest {
    private val cityWeatherDao: CityWeatherDao = mock()
    private val api: WeatherApi = mock()
    private val cityRepository: CityRepository = mock()

    private val repo = CityWeatherRepository(cityRepository, cityWeatherDao, api)

    @Rule
    @JvmField
    val schedulers = RxImmediateSchedulerRule()

    @Test
    fun getCityWeatherByCityIdTest() {
        val id = 123
        val cityWeather = createCityWeather()
        val city = createCityEntity()

        Mockito.`when`(cityWeatherDao.getCityWeatherByCityId(id)).thenReturn(Single.just(cityWeather))
        repo.getCityWeatherByCityId(city)
                .test()
                .assertValue(cityWeather)

        Mockito.verify(cityWeatherDao).getCityWeatherByCityId(id)
        Mockito.verifyNoMoreInteractions(cityWeatherDao)
    }

    @Test
    fun fetchCityWeatherSuccessTest() {
        val id = 123
        val response = createWeatherResponseModel(id)
        Mockito.`when`(api.getWeatherWithId(id)).thenReturn(Single.just(response))

        val cityWeather = response.mapToWeatherEntityTest(currentTimeInSeconds())
        val city = response.mapToCityEntity()

        repo.fetchWeatherOfCity(city)
                .test()
                .assertValue(cityWeather)
                .dispose()
        Mockito.verify(api).getWeatherWithId(id)
        Mockito.verify(cityWeatherDao).update(cityWeather)
        Mockito.verifyNoMoreInteractions(api)
        Mockito.verifyNoMoreInteractions(cityWeatherDao)
    }

    @Test
    fun fetchCityWeatherFailTest() {
        val id = 123
        val response = createWeatherResponseModel(id)
        val error = Error("Something went wrong")

        Mockito.`when`(api.getWeatherWithId(id)).thenReturn(Single.error(error))

        val cityWeather = response.mapToWeatherEntityTest(currentTimeInSeconds())
        val city = response.mapToCityEntity()

        repo.fetchWeatherOfCity(city)
                .test()
                .assertError(error)
                .dispose()

        Mockito.verify(api).getWeatherWithId(id)
        Mockito.verify(cityWeatherDao, never()).update(cityWeather)
    }

    @Test
    fun fetchCityWeatherLatLongTest() {
        val lat = 123.0
        val long = 456.0
        val response = createWeatherResponseModel()
        Mockito.`when`(api.getWeatherWithLatLong(lat = lat, long = long)).thenReturn(Single.just(response))

        val cityWeather = response.mapToWeatherEntityTest(currentTimeInSeconds())
        val city = response.mapToCityEntity()

        repo.fetchWeatherWithLatLong(lat, long)
                .test()
                .assertValue(cityWeather)
                .dispose()

        Mockito.verify(api).getWeatherWithLatLong(lat, long)
        Mockito.verify(cityWeatherDao).upsert(cityWeather)
        Mockito.verify(cityRepository).saveCity(city)
        Mockito.verifyNoMoreInteractions(api)
        Mockito.verifyNoMoreInteractions(cityWeatherDao)
        Mockito.verifyNoMoreInteractions(cityRepository)
    }
}