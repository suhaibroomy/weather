package com.suroid.weatherapp.utils.extensions

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import java.io.IOException
import java.nio.charset.Charset

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

fun Context.loadJSONFromAsset(fileName: String): String? {
    return try {
        val inputStream = assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        String(buffer, Charset.defaultCharset())
    } catch (ex: IOException) {
        ex.printStackTrace()
        null
    }
}