package com.suroid.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * BaseViewModel that auto Injects the required dependencies provided that corresponding
 * child ViewModel injection is added in ViewModelInjectors
 */
abstract class BaseViewModel : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    fun launch(job: () -> Disposable) {
        compositeDisposable.add(job())
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}