package com.suroid.weatherapp.ui.cityselection

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import android.widget.Filter
import com.suroid.weatherapp.BaseViewModel
import com.suroid.weatherapp.models.City
import com.suroid.weatherapp.models.CityWeatherEntity
import com.suroid.weatherapp.models.WeatherModel
import com.suroid.weatherapp.repo.CityRepository
import com.suroid.weatherapp.repo.CityWeatherRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class CitySelectionViewModel : BaseViewModel() {

    /**
     * Injects the required [CityRepository] in this ViewModel.
     */
    @Inject
    lateinit var cityRepository: CityRepository

    /**
     * Injects the required [CityWeatherRepository] in this ViewModel.
     */
    @Inject
    lateinit var cityWeatherRepository: CityWeatherRepository

    val queryText: MutableLiveData<String> = MutableLiveData()
    val cityListLiveData: MutableLiveData<List<City>> = MutableLiveData()
    private val cityList: ArrayList<City> = ArrayList()
    private val searchFilter = SearchFilter()


    init {
        val cityListDisposable = cityRepository.getAllCities()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onCitiesFetched(it)
                }, {
                    onError(it)
                })
        compositeDisposable.add(cityListDisposable)
    }

    private fun onError(throwable: Throwable) {
        //TODO handle error case
        Log.d(CitySelectionViewModel::class.java.name, throwable.message)
    }

    private fun onCitiesFetched(cityList: List<City>) {
        this.cityList.addAll(cityList)
        cityListLiveData.value = cityList
    }

    /**
     * Resets the search query and all related states
     */
    fun refreshData() {
        queryText.value = ""
        cityListLiveData.value = cityList
    }

    /**
     * Search for the city corresponding to the query
     * @param query Query to be searched
     */
    fun searchForCities(query: String) {
        if (query.isEmpty()) {
            cityListLiveData.value = cityList
        } else {
            searchFilter.filter(query)
        }
    }

    /**
     * Saves the provided city in WeatherDb
     * @param city city to be added
     */
    fun saveCity(city: City) {
        val cityWeather = CityWeatherEntity(id = city.id, city = city, currentWeather = WeatherModel())
        cityWeatherRepository.saveCityWeather(cityWeather)
    }

    @Suppress("UNCHECKED_CAST")
    private inner class SearchFilter : Filter() {
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            (results?.values as? ArrayList<City>)?.let {
                cityListLiveData.value = it
            }
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<City>()

            for (city in cityList) {
                if (city.name.toLowerCase().contains(constraint.toString().toLowerCase())) {
                    filteredList.add(city)
                    continue
                }
            }

            val results = Filter.FilterResults()
            results.values = filteredList
            results.count = filteredList.size
            return results
        }
    }

}

