package com.suroid.weatherapp.ui.cityselection

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.suroid.weatherapp.R
import com.suroid.weatherapp.TestApp
import com.suroid.weatherapp.models.CityEntity
import com.suroid.weatherapp.utils.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class CitySelectionActivityTest {
    private var viewModel = TestApp.citySelectionViewModel

    private val queryText = MutableLiveData<String>()
    private val cityListLiveData = MutableLiveData<List<CityEntity>>()
    private val citySelectedLivaData = LiveEvent<Boolean>()

    lateinit var activityScenario: ActivityScenario<CitySelectionActivity>

    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()

    @Rule
    @JvmField
    val animationsRule: DisableAnimationsRule = DisableAnimationsRule(ApplicationProvider.getApplicationContext())

    @Rule
    @JvmField
    val schedulers = RxImmediateSchedulerRule()

    @Before
    fun init() {
        Mockito.`when`(viewModel.cityEntityListLiveData).thenReturn(cityListLiveData)
        Mockito.`when`(viewModel.queryText).thenReturn(queryText)
        Mockito.`when`(TestApp.citySelectionViewModel.citySelectedLivaData).thenReturn(citySelectedLivaData)

        activityScenario = ActivityScenario.launch(CitySelectionActivity::class.java)

        Mockito.reset(viewModel)
    }

    @Test
    fun displayListTest() {
        val city = createCity()
        val list = arrayListOf(city)
        cityListLiveData.postValue(list)

        onView(withId(R.id.recycler_view))
                .check(matches(withViewAtPosition(0, hasDescendant(
                        withText(ApplicationProvider.getApplicationContext<Context>()
                                .getString(R.string.city_country_format, city.name, city.country))))))
    }

    @Test
    fun clickOnCityTest() {
        val city = createCity()
        val list = arrayListOf(city)
        cityListLiveData.postValue(list)

        onView(withId(R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition<CitySelectionAdapter.CityViewHolder>(0, click()))

        Mockito.verify(viewModel).saveSelectedCity(city)
    }

    @Test
    fun queryTest() {
        val query = "sample Query"

        onView(withId(R.id.et_search))
                .perform(replaceText(query))

        Mockito.verify(viewModel).searchForCities(query)
    }


    @After
    fun destroy() {
        activityScenario.close()
    }
}