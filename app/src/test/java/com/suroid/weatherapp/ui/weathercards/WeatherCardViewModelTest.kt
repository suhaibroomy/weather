package com.suroid.weatherapp.ui.weathercards

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.suroid.weatherapp.repo.CityWeatherRepository
import com.suroid.weatherapp.util.RxImmediateSchedulerRule
import com.suroid.weatherapp.util.createCityEntity
import com.suroid.weatherapp.util.createCityWeather
import com.suroid.weatherapp.util.mock
import com.suroid.weatherapp.utils.WEATHER_EXPIRY_THRESHOLD_TIME
import com.suroid.weatherapp.utils.currentTimeInSeconds
import com.suroid.weatherapp.utils.weatherIconForId
import com.suroid.weatherapp.utils.weatherImageForId
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.times

@RunWith(JUnit4::class)
class WeatherCardViewModelTest {
    @Rule
    @JvmField
    val schedulers = RxImmediateSchedulerRule()

    @Rule
    @JvmField
    val architectureComponentsRule = InstantTaskExecutorRule()

    private val cityWeatherRepository = mock<CityWeatherRepository>()

    private val viewModel = WeatherCardViewModel(cityWeatherRepository)

    private val loadingStatusObserver = mock<(Boolean) -> Unit>()
    private val tempObserver = mock<(String) -> Unit>()
    private val cityObserver = mock<(String) -> Unit>()
    private val weatherTitleObserver = mock<(String?) -> Unit>()
    private val humidityObserver = mock<(String) -> Unit>()
    private val minMaxTempObserver = mock<(String) -> Unit>()
    private val windObserver = mock<(String) -> Unit>()
    private val iconObserver = mock<(Int) -> Unit>()
    private val imageObserver = mock<(Int) -> Unit>()

    private val city = createCityEntity()
    private val cityWeather = createCityWeather(currentTimeInSeconds())

    @Before
    fun setUp() {

        viewModel.loadingStatus.observeForever {
            it.let(loadingStatusObserver)
        }

        viewModel.temp.observeForever {
            it.let(tempObserver)
        }

        viewModel.city.observeForever {
            it.let(cityObserver)
        }

        viewModel.weatherTitle.observeForever {
            it.let(weatherTitleObserver)
        }

        viewModel.humidity.observeForever {
            it.let(humidityObserver)
        }

        viewModel.minMaxTemp.observeForever {
            it.let(minMaxTempObserver)
        }

        viewModel.wind.observeForever {
            it.let(windObserver)
        }

        viewModel.icon.observeForever {
            it.let(iconObserver)
        }

        viewModel.image.observeForever {
            it.let(imageObserver)
        }
    }

    @Test
    fun setupWithCityFromDbTest() {
        Mockito.`when`(cityWeatherRepository.getCityWeatherByCityId(city)).thenReturn(Single.just(cityWeather))


        viewModel.setupWithCity(city)

        Mockito.verify(cityWeatherRepository).getCityWeatherByCityId(city)
        Mockito.verify(loadingStatusObserver).invoke(true)
        Mockito.verify(loadingStatusObserver).invoke(false)

        verifyObservables(1)
    }

    @Test
    fun setupWithCityFromDbFailTest() {
        Mockito.`when`(cityWeatherRepository.getCityWeatherByCityId(city)).thenReturn(Single.error(Error("not found")))
        Mockito.`when`(cityWeatherRepository.fetchWeatherOfCity(city)).thenReturn(Single.just(cityWeather))

        viewModel.setupWithCity(city)

        Mockito.verify(cityWeatherRepository).fetchWeatherOfCity(city)
        Mockito.verify(loadingStatusObserver, times(2)).invoke(true)
        Mockito.verify(loadingStatusObserver).invoke(false)

        verifyObservables(1)
    }

    @Test
    fun setupWithStaleCityFromDbTest() {
        val cityWeather = createCityWeather(currentTimeInSeconds() - WEATHER_EXPIRY_THRESHOLD_TIME)
        Mockito.`when`(cityWeatherRepository.getCityWeatherByCityId(city)).thenReturn(Single.just(cityWeather))
        Mockito.`when`(cityWeatherRepository.fetchWeatherOfCity(city)).thenReturn(Single.just(cityWeather))

        viewModel.setupWithCity(city)

        Mockito.verify(cityWeatherRepository).fetchWeatherOfCity(city)
        Mockito.verify(loadingStatusObserver, times(2)).invoke(true)
        Mockito.verify(loadingStatusObserver, times(2)).invoke(false)

        verifyObservables(2)
    }

    private fun verifyObservables(times: Int) {
        cityWeather.currentWeather?.let {
            it.temperature?.let { temp ->
                Mockito.verify(tempObserver, times(times)).invoke(temp.temp?.toInt().toString())
                Mockito.verify(minMaxTempObserver, times(times)).invoke("${temp.minTemp?.toInt()}/${temp.maxTemp?.toInt()}")
            }
            Mockito.verify(humidityObserver, times(times)).invoke(it.humidity.toString())
            Mockito.verify(windObserver, times(times)).invoke(it.windSpeed.toString())
            Mockito.verify(weatherTitleObserver, times(times)).invoke(it.title)
            Mockito.verify(iconObserver, times(times)).invoke(weatherIconForId(it.weather_id ?: 0))
            Mockito.verify(imageObserver, times(times)).invoke(weatherImageForId(it.weather_id ?: 0))
        }
        Mockito.verify(cityObserver, times(times)).invoke("${city.name}, ${city.country}")

    }


}