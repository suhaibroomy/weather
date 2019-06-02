package com.suroid.weatherapp.ui.home

import androidx.lifecycle.MutableLiveData
import android.location.Location
import android.util.Log
import com.suroid.weatherapp.models.CityEntity
import com.suroid.weatherapp.repo.CityRepository
import com.suroid.weatherapp.repo.CityWeatherRepository
import com.suroid.weatherapp.utils.Mockable
import com.suroid.weatherapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * @Inject Injects the required [CityWeatherRepository] in this ViewModel.
 */
@Mockable
class HomeViewModel @Inject constructor(private val cityWeatherRepository: CityWeatherRepository, private val cityRepository: CityRepository) : BaseViewModel() {

    val cityListLiveData: MutableLiveData<ArrayList<CityEntity>> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val fetchCityResult: MutableLiveData<Boolean> = MutableLiveData()

    init {
        compositeDisposable.add(
                cityRepository.getSelectedCities()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            onCitiesFetched(it)
                        }, {
                            onError(it)
                        }))
    }

    private fun onCitiesFetched(cityList: List<CityEntity>) {
        cityListLiveData.value = ArrayList(cityList)
    }

    private fun onError(t: Throwable?) {
        //TODO handle error case
        Log.d(HomeViewModel::class.java.name, t?.message)
    }

    /**
     * fetches the cityWeather for the location provided
     * @param location location object of the place
     */
    fun fetchForCurrentLocation(location: Location) {
        compositeDisposable.add(
                cityWeatherRepository.fetchWeatherWithLatLong(location.latitude, location.longitude)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe {
                            loading.value = true
                        }
                        .doFinally {
                            loading.value = false
                        }
                        .doOnSuccess {
                            cityRepository.saveSelectedCity(it.cityId)
                        }
                        .subscribe())

    }
}