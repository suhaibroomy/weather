package com.suroid.weatherapp.ui.cityselection

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.suroid.weatherapp.models.CityEntity
import com.suroid.weatherapp.repo.CityRepository
import com.suroid.weatherapp.utils.LiveEvent
import com.suroid.weatherapp.utils.Mockable
import com.suroid.weatherapp.viewmodel.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * @Inject Injects the required [CityRepository] in this ViewModel.
 */
@Mockable
class CitySelectionViewModel @Inject constructor(private val cityRepository: CityRepository) : BaseViewModel() {

    val queryText: MutableLiveData<String> = MutableLiveData()
    val cityEntityListLiveData: MutableLiveData<List<CityEntity>> = MutableLiveData()
    val citySelectedLivaData = LiveEvent<Boolean>()
    private val cityEntityList: ArrayList<CityEntity> = ArrayList()


    init {
        launch {
            cityRepository.getAllCities()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        onCitiesFetched(it)
                    }, {
                        onError(it)
                    })
        }
    }

    private fun onError(throwable: Throwable) {
        //TODO handle error case
        Log.d(CitySelectionViewModel::class.java.name, throwable.message)
    }

    private fun onCitiesFetched(cityEntityList: List<CityEntity>) {
        this.cityEntityList.addAll(cityEntityList)
        cityEntityListLiveData.value = cityEntityList
    }

    /**
     * Resets the search query and all related states
     */
    fun refreshData() {
        queryText.value = ""
        cityEntityListLiveData.value = cityEntityList
    }

    /**
     * Search for the city corresponding to the query
     * @param query [String]Query to be searched
     */
    fun searchForCities(query: String) {
        launch {
            Observable.just(query)
                    .flatMapSingle {
                        if (it.isEmpty()) {
                            cityRepository.getAllCities()
                        } else {
                            cityRepository.searchForCity("$query%")
                        }
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        cityEntityListLiveData.value = it
                    }, {
                        onError(it)
                    })

        }
    }

    /**
     * Mark cityEntity as selected in the db
     * @param cityEntity [CityEntity] to saved
     */
    fun saveSelectedCity(cityEntity: CityEntity) {
        launch {
            cityRepository.saveSelectedCity(cityEntity.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        citySelectedLivaData.value = true
                    }, {
                        onError(it)
                    })
        }
    }
}

