package com.suroid.weatherapp.utils.extensions

import android.graphics.drawable.TransitionDrawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat


fun ImageView.fadeInImage(@DrawableRes drawableRes: Int) {
    ContextCompat.getDrawable(context, drawableRes)?.let {
        val td = TransitionDrawable(arrayOf(this.drawable, it))
        this.setImageDrawable(td)
        td.isCrossFadeEnabled = true
        td.startTransition(1000)
    }
}

fun Group.setAllOnClickListener(setter: (View) -> Unit) {
    referencedIds.forEach { id ->
        setter(rootView.findViewById<View>(id))
    }
}
