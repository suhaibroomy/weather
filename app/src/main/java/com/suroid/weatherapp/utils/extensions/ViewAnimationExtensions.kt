package com.suroid.weatherapp.utils.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat


/**
 * Performs reveal hide animation on the passed view and provides callbacks to the listener
 * @param color color for reveal Animation. This should be a valid color resource
 * @param finalRadius radius for reveal animation
 * @param onComplete callback for completion
 */
fun View.animateRevealHide(@ColorRes color: Int, finalRadius: Int, onComplete: () -> Unit) {
    val cx = (left + right) / 2
    val cy = (top + bottom) / 2
    val initialRadius = width

    val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, initialRadius.toFloat(), finalRadius.toFloat())
    anim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            super.onAnimationStart(animation)
            setBackgroundColor(ContextCompat.getColor(context, color))
        }

        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            onComplete()
            visibility = View.INVISIBLE
        }
    })
    anim.duration = 300
    anim.start()
}

/**
 * Performs reveal show animation on the passed view and provides callbacks to the listener
 * @param color color for reveal Animation. This shoud be a valid color resource
 * @param x PivotX for the view
 * @param y PivotY for the view
 */
fun View.animateRevealShow(startRadius: Int, @ColorRes color: Int, x: Int, y: Int, onComplete: () -> Unit) {
    val finalRadius = Math.hypot(width.toDouble(), height.toDouble()).toFloat()

    val anim = ViewAnimationUtils.createCircularReveal(this, x, y, startRadius.toFloat(), finalRadius)
    anim.duration = 300
    anim.startDelay = 100
    anim.interpolator = AccelerateDecelerateInterpolator()
    anim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            setBackgroundColor(ContextCompat.getColor(context, color))
        }

        override fun onAnimationEnd(animation: Animator) {
            visibility = View.VISIBLE
            onComplete()
        }
    })
    anim.start()
}