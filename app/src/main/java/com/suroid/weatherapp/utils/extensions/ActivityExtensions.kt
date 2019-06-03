package com.suroid.weatherapp.utils.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.suroid.weatherapp.R

fun Activity.showPermissionDialog(showRationale: Boolean, cancel: () -> Unit) {
    if (showRationale) {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.permissions_needed_dialog_title))
                .setMessage(getString(R.string.permissions_rationale_dialog_message))
                .setPositiveButton(R.string.retry) { _, _ -> }
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