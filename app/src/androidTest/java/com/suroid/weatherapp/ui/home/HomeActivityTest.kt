package com.suroid.weatherapp.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.GrantPermissionRule
import com.suroid.weatherapp.R
import com.suroid.weatherapp.TestApp
import com.suroid.weatherapp.models.local.CityEntity
import com.suroid.weatherapp.ui.cityselection.CitySelectionActivity
import com.suroid.weatherapp.utils.DisableAnimationsRule
import com.suroid.weatherapp.utils.LiveEvent
import com.suroid.weatherapp.utils.TaskExecutorWithIdlingResourceRule
import com.suroid.weatherapp.utils.createCity
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.times

class HomeActivityTest {

    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()

    private var viewModel = TestApp.homeViewModel

    private val loading = MutableLiveData<Boolean>()
    private val showErrorMessage = LiveEvent<Int>()
    private val cityListLiveData = MutableLiveData<List<CityEntity>>()

    @Rule
    @JvmField
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION)

    @Rule
    @JvmField
    val animationsRule: DisableAnimationsRule = DisableAnimationsRule(ApplicationProvider.getApplicationContext())

    private lateinit var activityScenario: ActivityScenario<HomeActivity>

    @Before
    fun init() {
        Mockito.`when`(viewModel.loading).thenReturn(loading)
        Mockito.`when`(viewModel.showErrorMessage).thenReturn(showErrorMessage)
        Mockito.`when`(viewModel.cityListLiveData).thenReturn(cityListLiveData)

        Mockito.`when`(TestApp.citySelectionViewModel.cityEntityListLiveData).thenReturn(MutableLiveData())
        Mockito.`when`(TestApp.citySelectionViewModel.queryText).thenReturn(MutableLiveData())
        Mockito.`when`(TestApp.citySelectionViewModel.citySelectedLivaData).thenReturn(LiveEvent())

        Mockito.`when`(TestApp.weatherCardViewModel.loadingStatus).thenReturn(MutableLiveData())
        Mockito.`when`(TestApp.weatherCardViewModel.icon).thenReturn(MutableLiveData())
        Mockito.`when`(TestApp.weatherCardViewModel.image).thenReturn(MutableLiveData())
        Mockito.`when`(TestApp.weatherCardViewModel.temp).thenReturn(MutableLiveData())
        Mockito.`when`(TestApp.weatherCardViewModel.minMaxTemp).thenReturn(MutableLiveData())
        Mockito.`when`(TestApp.weatherCardViewModel.humidity).thenReturn(MutableLiveData())
        Mockito.`when`(TestApp.weatherCardViewModel.weatherTitle).thenReturn(MutableLiveData())
        Mockito.`when`(TestApp.weatherCardViewModel.wind).thenReturn(MutableLiveData())
        Mockito.`when`(TestApp.weatherCardViewModel.city).thenReturn(MutableLiveData())

        activityScenario = ActivityScenario.launch(HomeActivity::class.java)
        Intents.init()
    }

    @Test
    fun launchSelectionActivityTest() {
        onView(withId(R.id.fab))
                .perform(click())

        intended(hasComponent(CitySelectionActivity::class.java.name))
    }

    @Test
    fun clickFetchLocationTest() {
        cityListLiveData.postValue(ArrayList())
        onView(withId(R.id.tv_welcome)).perform(click())
        onView(withId(R.id.iv_welcome)).perform(click())

        Mockito.verify(viewModel, times(2)).fetchForCurrentLocation()
    }

    @Test
    fun startLoadingTest() {
        viewModel.loading.postValue(true)
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
    }

    @Test
    fun stopLoadingTest() {
        viewModel.loading.postValue(false)
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
    }

    @Test
    fun addCardTest() {
        val arr = arrayListOf(createCity())
        viewModel.cityListLiveData.postValue(arr)
        onView(allOf(withId(R.id.weather_card_root))).check(matches(isCompletelyDisplayed()))
    }

    @After
    fun destroy() {
        Intents.release()
        activityScenario.close()
    }
}