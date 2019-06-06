package com.suroid.weatherapp.utils.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.suroid.weatherapp.R

fun Activity.showPermissionDialog(showRationale: Boolean, retry: () -> Unit, cancel: () -> Unit) {
    if (showRationale) {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.permissions_needed_dialog_title))
                .setMessage(getString(R.string.permissions_rationale_dialog_message))
                .setPositiveButton(R.string.retry) { _, _ -> retry() }
                .setNegativeButton(R.string.cancel) { _, _ -> cancel() }
                .create()
                .show()
    } else {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.permissions_needed_dialog_title))
                .setMessage(getString(R.string.permissions_needed_dialog_message))
                .setPositiveButton(R.string.settings) { _, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")).apply {
                        addCategory(Intent.CATEGORY_DEFAULT)
                    }
                    startActivity(intent)
                }
                .setNegativeButton(R.string.cancel) { _, _ -> cancel() }
                .create()
                .show()
    }
}

fun Activity.hideKeyboard(view: View?) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    view?.let {
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    } ?: run {
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}

fun Activity.showKeyboard(view: View?) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    view?.let {
        imm.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
    } ?: run {
        currentFocus?.let {
            imm.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}