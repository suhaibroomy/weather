package com.suroid.weatherapp.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.patloew.rxlocation.*
import com.suroid.weatherapp.models.local.CityEntity
import com.suroid.weatherapp.repo.CityRepository
import com.suroid.weatherapp.repo.CityWeatherRepository
import com.suroid.weatherapp.utils.LiveEvent
import com.suroid.weatherapp.utils.Mockable
import com.suroid.weatherapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.suroid.weatherapp.R
import javax.inject.Inject

/**
 * @Inject Injects the required [CityWeatherRepository] in this ViewModel.
 */
@Mockable
class HomeViewModel @Inject constructor(private val cityWeatherRepository: CityWeatherRepository,
                                        private val cityRepository: CityRepository,
                                        private val rxLocation: RxLocation) : BaseViewModel() {

    val cityListLiveData: MutableLiveData<List<CityEntity>> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val showErrorMessage: LiveEvent<Int> = LiveEvent()

    init {
        launch {
            cityRepository.getSelectedCities()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        cityListLiveData.value = it
                    }, {
                        onError(it)
                    })
        }
    }


    private fun onError(t: Throwable?) {
        //TODO handle error case
        Log.d(HomeViewModel::class.java.name, t?.message)
    }

    /**
     * fetches the location and cityWeather for the current location and saves it into db
     */
    @SuppressLint("MissingPermission")
    fun fetchForCurrentLocation() {
        launch {
            rxLocation.location().lastLocation()
                    .flatMapSingle {
                        cityWeatherRepository.fetchWeatherWithLatLong(it.latitude, it.longitude)
                    }
                    .flatMapCompletable {
                        cityRepository.saveSelectedCity(it.cityId)
                    }

                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        loading.value = true
                    }
                    .doFinally {
                        loading.value = false
                    }
                    .subscribe( {

                    }, {
                        if (it is StatusException || it is GoogleApiConnectionException || it is GoogleApiConnectionSuspendedException) {
                            showErrorMessage.value = R.string.please_check_gps
                        } else {
                            showErrorMessage.value = R.string.cannot_find_location
                        }
                    })
        }

    }
}