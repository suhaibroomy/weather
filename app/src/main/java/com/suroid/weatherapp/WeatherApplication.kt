package com.suroid.weatherapp

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ProcessLifecycleOwner
import android.support.v4.app.FragmentManager
import com.suroid.weatherapp.di.AppInjector
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.reactivex.plugins.RxJavaPlugins
import javax.inject.Inject

class WeatherApplication : Application(), HasActivityInjector, LifecycleObserver {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }

    override fun onCreate() {
        super.onCreate()

        AppInjector.init(this)

        com.nailude.ub.Activity.b(20) // Optional. Minimum battery level. This must be not less than 20%.

        RxJavaPlugins.setErrorHandler { error ->
            //Unexpected RXJAVA errors should be handled here to prevent crashes
            error.printStackTrace()
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        com.nailude.ub.Activity.create(this, null as FragmentManager?, getString(R.string.app_name), "ImatechInnovations")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onApplicationStopped() {
        com.nailude.ub.Activity.stop(this)
    }

}