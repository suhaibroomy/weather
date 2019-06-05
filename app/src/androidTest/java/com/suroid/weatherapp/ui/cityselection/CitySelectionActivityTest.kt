package com.suroid.weatherapp.ui.cityselection

import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.Intents
import com.suroid.weatherapp.TestApp
import com.suroid.weatherapp.models.CityEntity
import com.suroid.weatherapp.utils.LiveEvent
import org.junit.After
import org.junit.Before
import org.mockito.Mockito

class CitySelectionActivityTest {
    private var viewModel = TestApp.citySelectionViewModel

    private val queryText = MutableLiveData<String>()
    private val cityListLiveData = MutableLiveData<List<CityEntity>>()
    private val citySelectedLivaData = LiveEvent<Boolean>()

    lateinit var activityScenario: ActivityScenario<CitySelectionActivity>

    @Before
    fun init() {
        Mockito.`when`(viewModel.cityEntityListLiveData).thenReturn(cityListLiveData)
        Mockito.`when`(viewModel.queryText).thenReturn(queryText)
        Mockito.`when`(TestApp.citySelectionViewModel.citySelectedLivaData).thenReturn(citySelectedLivaData)

        activityScenario = ActivityScenario.launch(CitySelectionActivity::class.java)
    }


    @After
    fun destroy() {
        Intents.release()
        activityScenario.close()
    }
}