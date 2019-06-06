package com.suroid.weatherapp.ui.weathercards

import androidx.lifecycle.MutableLiveData
import com.suroid.weatherapp.models.local.CityEntity
import com.suroid.weatherapp.models.local.CityWeatherEntity
import com.suroid.weatherapp.repo.CityWeatherRepository
import com.suroid.weatherapp.utils.*
import com.suroid.weatherapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * @Inject Injects the required [CityWeatherRepository] in this ViewModel.
 */
@Mockable
class WeatherCardViewModel @Inject constructor(private val cityWeatherRepository: CityWeatherRepository) : BaseViewModel() {

    val loadingStatus: MutableLiveData<Boolean> = MutableLiveData()
    val temp: MutableLiveData<String> = MutableLiveData()
    val city: MutableLiveData<String> = MutableLiveData()
    val weatherTitle: MutableLiveData<String> = MutableLiveData()
    val humidity: MutableLiveData<String> = MutableLiveData()
    val minMaxTemp: MutableLiveData<String> = MutableLiveData()
    val wind: MutableLiveData<String> = MutableLiveData()
    val icon: MutableLiveData<Int> = MutableLiveData()
    val image: MutableLiveData<Int> = MutableLiveData()

    /**
     * Setup this card with the provided city
     * @param cityEntity [CityEntity] to be setup
     */
    fun setupWithCity(cityEntity: CityEntity) {
        launch {
            cityWeatherRepository
                    .getCityWeatherByCityId(cityEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        loadingStatus.value = true
                    }
                    .doOnSuccess {
                        loadingStatus.value = false
                    }
                    .subscribe({
                        updateValues(it, cityEntity)
                        if (currentTimeInSeconds() - it.date >= WEATHER_EXPIRY_THRESHOLD_TIME) {
                            updateCityWeather(cityEntity)
                        }
                    }, {
                        updateCityWeather(cityEntity)
                    })
        }
    }

    private fun updateCityWeather(cityEntity: CityEntity) {
        launch {
            cityWeatherRepository.fetchWeatherOfCity(cityEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        loadingStatus.value = true
                    }
                    .doFinally {
                        loadingStatus.value = false
                    }
                    .subscribe({
                        updateValues(it, cityEntity)
                    }, {
                        //TODO handle error
                    })
        }

    }

    private fun updateValues(cityWeather: CityWeatherEntity, cityEntity: CityEntity) {
        cityWeather.currentWeather?.let {
            it.temperature?.let { temperature ->
                temp.value = temperature.temp?.toInt().toString()
                minMaxTemp.value = "${temperature.minTemp?.toInt()}/${temperature.maxTemp?.toInt()}"
            }
            humidity.value = it.humidity?.toString()
            wind.value = it.windSpeed?.toString()
            weatherTitle.value = it.title
            icon.value = weatherIconForId(it.weather_id ?: 0)
            image.value = weatherImageForId(it.weather_id ?: 0)

        }
        city.value = "${cityEntity.name}, ${cityEntity.country}"
    }

}