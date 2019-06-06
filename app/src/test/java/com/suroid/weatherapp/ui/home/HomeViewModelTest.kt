package com.suroid.weatherapp.ui.home

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.patloew.rxlocation.FusedLocation
import com.patloew.rxlocation.RxLocation
import com.patloew.rxlocation.StatusException
import com.suroid.weatherapp.R
import com.suroid.weatherapp.models.CityEntity
import com.suroid.weatherapp.repo.CityRepository
import com.suroid.weatherapp.repo.CityWeatherRepository
import com.suroid.weatherapp.util.RxImmediateSchedulerRule
import com.suroid.weatherapp.util.createCityEntity
import com.suroid.weatherapp.util.createCityWeather
import com.suroid.weatherapp.util.mock
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.never

@RunWith(JUnit4::class)
class HomeViewModelTest {
    @Rule
    @JvmField
    val schedulers = RxImmediateSchedulerRule()

    @Rule
    @JvmField
    val architectureComponentsRule = InstantTaskExecutorRule()

    private val cityRepository = mock<CityRepository>()
    private val cityWeatherRepository = mock<CityWeatherRepository>()
    private val rxLocation = mock<RxLocation>()

    private lateinit var viewModel: HomeViewModel

    private lateinit var cityList: ArrayList<CityEntity>

    private val cityListObserver = mock<(List<CityEntity>) -> Unit>()
    private val loadingObserver = mock<(Boolean) -> Unit>()
    private val errorMessageObserver = mock<(Int) -> Unit>()

    @Before
    fun setUp() {
        cityList = arrayListOf(createCityEntity())

        Mockito.`when`(cityRepository.getSelectedCities()).thenReturn(Flowable.just(cityList))
        viewModel = HomeViewModel(cityWeatherRepository, cityRepository, rxLocation)

        viewModel.cityListLiveData.observeForever {
            it.let(cityListObserver)
        }
        viewModel.loading.observeForever {
            it.let(loadingObserver)
        }
        viewModel.showErrorMessage.observeForever {
            it.let(errorMessageObserver)
        }
        Mockito.verify(cityListObserver).invoke(cityList)
        Mockito.reset(cityListObserver)
        Mockito.reset(loadingObserver)
    }


    @Test
    fun fetchWeatherForCurrentLocationTest() {
        val cityWeatherEntity = createCityWeather()
        val fakeLocation = mock<Location>()
        Mockito.`when`(fakeLocation.latitude).thenReturn(28.530326302293503)
        Mockito.`when`(fakeLocation.longitude).thenReturn(77.21394667401911)
        val fusedLocation = mock<FusedLocation>()
        Mockito.`when`(rxLocation.location()).thenReturn(fusedLocation)
        Mockito.`when`(fusedLocation.lastLocation()).thenReturn(Maybe.just(fakeLocation))
        Mockito.`when`(cityWeatherRepository.fetchWeatherWithLatLong(fakeLocation.latitude, fakeLocation.longitude)).thenReturn(Single.just(cityWeatherEntity))
        Mockito.`when`(cityRepository.saveSelectedCity(cityWeatherEntity.cityId)).thenReturn(Completable.complete())

        viewModel.fetchForCurrentLocation()

        Mockito.verify(cityWeatherRepository).fetchWeatherWithLatLong(fakeLocation.latitude, fakeLocation.longitude)
        Mockito.verify(cityRepository).saveSelectedCity(cityWeatherEntity.cityId)
        Mockito.verify(loadingObserver).invoke(true)
        Mockito.verify(loadingObserver).invoke(false)
    }

    @Test
    fun fetchCurrentLocationFailTest() {
        val fusedLocation = mock<FusedLocation>()
        Mockito.`when`(rxLocation.location()).thenReturn(fusedLocation)
        Mockito.`when`(fusedLocation.lastLocation()).thenReturn(Maybe.error(mock<StatusException>()))

        viewModel.fetchForCurrentLocation()

        Mockito.verify(errorMessageObserver).invoke(R.string.please_check_gps)
        Mockito.verify(cityRepository, never()).saveSelectedCity(ArgumentMatchers.anyInt())
        Mockito.verify(cityWeatherRepository, never()).fetchWeatherWithLatLong(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyDouble())
        Mockito.verify(loadingObserver).invoke(false)
    }

    @Test
    fun weatherWithLocationFetchFailTest() {
        val fakeLocation = mock<Location>()
        Mockito.`when`(fakeLocation.latitude).thenReturn(28.530326302293503)
        Mockito.`when`(fakeLocation.longitude).thenReturn(77.21394667401911)
        val fusedLocation = mock<FusedLocation>()
        Mockito.`when`(rxLocation.location()).thenReturn(fusedLocation)
        Mockito.`when`(fusedLocation.lastLocation()).thenReturn(Maybe.just(fakeLocation))
        Mockito.`when`(cityWeatherRepository.fetchWeatherWithLatLong(fakeLocation.latitude, fakeLocation.longitude)).thenReturn(Single.error(Error("network error")))

        viewModel.fetchForCurrentLocation()

        Mockito.verify(errorMessageObserver).invoke(R.string.cannot_find_location)
        Mockito.verify(cityWeatherRepository).fetchWeatherWithLatLong(fakeLocation.latitude, fakeLocation.longitude)
        Mockito.verify(cityRepository, never()).saveSelectedCity(ArgumentMatchers.anyInt())
        Mockito.verify(loadingObserver).invoke(false)
    }

}