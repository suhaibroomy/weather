package com.suroid.weatherapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.TransitionDrawable
import android.net.Uri
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.suroid.weatherapp.R
import com.suroid.weatherapp.models.CityEntity
import com.suroid.weatherapp.models.CityWeatherEntity
import com.suroid.weatherapp.models.TemperatureModel
import com.suroid.weatherapp.models.WeatherModel
import com.suroid.weatherapp.models.remote.WeatherResponseModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.lang.reflect.Type

val gson = Gson()

fun <T> String.objectify(type: Type): T? {
    if (this.isEmpty()) {
        return null
    }
    try {
        return gson.fromJson<T>(this, type)
    } catch (ignored: Exception) {
    }

    return null
}

fun Any.jsonify(): String? {
    try {
        return gson.toJson(this)
    } catch (ignored: Exception) {
    }

    return null
}

fun ImageView.fadeInImage(@DrawableRes drawableRes: Int) {
    ContextCompat.getDrawable(context, drawableRes)?.let {
        val td = TransitionDrawable(arrayOf(this.drawable, it))
        this.setImageDrawable(td)
        td.isCrossFadeEnabled = true
        td.startTransition(1000)
    }
}

fun Activity.checkAndAskPermissions(permission: String): Boolean {
    if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
        return true
    }

    ActivityCompat.requestPermissions(this, arrayOf(permission), 0)

    return false
}

fun Activity.handlePermissionResult(permission: String, success: () -> Unit) {
    if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
        success()
    } else {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder(this).setTitle(getString(R.string.permissions_needed_dialog_title)).setMessage(getString(R.string.permissions_needed_dialog_message)).setPositiveButton(R.string.settings) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")).apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                }
                startActivity(intent)
            }.setNegativeButton(R.string.cancel) { _, _ -> }.create().show()
        } else {
            AlertDialog.Builder(this).setTitle(getString(R.string.permissions_needed_dialog_title)).setMessage(getString(R.string.permissions_rationale_dialog_message)).setPositiveButton(R.string.retry) { _, _ -> ActivityCompat.requestPermissions(this, arrayOf(permission), 0) }.setNegativeButton(R.string.cancel) { _, _ -> }.create().show()
        }
    }
}

@UiThread
fun Context.showToast(@StringRes message: Int) {
    val toast = Toast.makeText(this, getString(message), Toast.LENGTH_LONG)
    var toastTxv: TextView? = null
    val view = toast.view
    if (view is TextView) {
        toastTxv = view
    } else if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val child = view.getChildAt(i)
            if (child is TextView) {
                toastTxv = child
                break
            }
        }
    }
    toastTxv?.let {
        it.gravity = Gravity.CENTER_HORIZONTAL
    }
    toast.show()
}

/**
 * Extension function WeatherResponseModel to CityWeatherEntity
 * */
fun WeatherResponseModel.mapToWeatherEntity(cityEntity: CityEntity? = null): CityWeatherEntity {
    val temperatureModel = TemperatureModel(main.temp, main.temp_min, main.temp_max)
    val weatherModel = WeatherModel(getWeather()?.main, getWeather()?.description, temperatureModel, wind.speed, main.humidity, getWeather()?.id)
    return CityWeatherEntity(cityEntity?.id ?: id, weatherModel, date = currentTimeInSeconds())
}

/**
 * Extension function WeatherResponseModel to CityWeatherEntity
 * */
fun WeatherResponseModel.mapToCityEntity(): CityEntity {
    return CityEntity(name, sys.country, id)
}