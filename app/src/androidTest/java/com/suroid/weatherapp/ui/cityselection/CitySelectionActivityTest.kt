package com.suroid.weatherapp.ui.cityselection

import android.arch.lifecycle.MutableLiveData
import android.support.test.rule.ActivityTestRule
import com.suroid.weatherapp.TestApp
import com.suroid.weatherapp.models.CityEntity
import org.junit.Rule
import org.mockito.Mockito

class CitySelectionActivityTest {
    private var viewModel = TestApp.citySelectionViewModel

    private val queryText = MutableLiveData<String>()
    private val cityListLiveData = MutableLiveData<List<CityEntity>>()

    @Rule
    @JvmField
    val activityRule = object : ActivityTestRule<CitySelectionActivity>(CitySelectionActivity::class.java) {
        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()
            Mockito.`when`(viewModel.cityEntityListLiveData).thenReturn(cityListLiveData)
            Mockito.`when`(viewModel.queryText).thenReturn(queryText)
        }
    }
}