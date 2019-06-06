package com.suroid.weatherapp.ui.cityselection

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.suroid.weatherapp.models.CityEntity
import com.suroid.weatherapp.repo.CityRepository
import com.suroid.weatherapp.util.RxImmediateSchedulerRule
import com.suroid.weatherapp.util.createCityEntity
import com.suroid.weatherapp.util.mock
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito
import org.mockito.Mockito.never

@RunWith(JUnit4::class)
class CitySelectionViewModelTest {
    @Rule
    @JvmField
    val schedulers = RxImmediateSchedulerRule()

    @Rule
    @JvmField
    val architectureComponentsRule = InstantTaskExecutorRule()

    private val cityRepository = mock<CityRepository>()

    private lateinit var viewModel: CitySelectionViewModel

    private val cityList = arrayListOf(createCityEntity())

    private val cityListObserver = mock<(List<CityEntity>) -> Unit>()
    private val queryStringObserver = mock<(String) -> Unit>()

    @Before
    fun setUp() {
        Mockito.`when`(cityRepository.getAllCities()).thenReturn(Single.just(cityList))
        viewModel = CitySelectionViewModel(cityRepository)

        viewModel.cityEntityListLiveData.observeForever {
            it.let(cityListObserver)
        }

        viewModel.queryText.observeForever {
            it.let(queryStringObserver)
        }

        Mockito.verify(cityListObserver).invoke(cityList)
        Mockito.reset(cityListObserver)
        Mockito.reset(cityRepository)
    }

    @Test
    fun searchFailTest() {
        val query = "abcd"

        val result = arrayListOf<CityEntity>()
        Mockito.`when`(cityRepository.searchForCity("$query%")).thenReturn(Single.just(result))

        viewModel.searchForCities(query)

        Mockito.verify(cityRepository).searchForCity("$query%")
        Mockito.verify(cityRepository, never()).getAllCities()
        Mockito.verify(cityListObserver).invoke(result)
    }

    @Test
    fun searchSuccessTest() {
        val query = "name"

        Mockito.`when`(cityRepository.searchForCity("$query%")).thenReturn(Single.just(cityList))

        viewModel.searchForCities(query)

        Mockito.verify(cityRepository).searchForCity("$query%")
        Mockito.verify(cityRepository, never()).getAllCities()
        Mockito.verify(cityListObserver).invoke(cityList)
    }

    @Test
    fun emptyStringSearchTest() {
        val query = ""

        Mockito.`when`(cityRepository.getAllCities()).thenReturn(Single.just(cityList))

        viewModel.searchForCities(query)

        Mockito.verify(cityRepository, never()).searchForCity(anyString())
        Mockito.verify(cityRepository).getAllCities()
        Mockito.verify(cityListObserver).invoke(cityList)
    }

    @Test
    fun resetAfterSearchTest() {
        viewModel.refreshData()

        Mockito.verify(queryStringObserver).invoke("")
    }


}