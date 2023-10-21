package com.s0mt0chukwu.coom.extensions

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.view.animation.AlphaAnimation
import org.jetbrains.anko.backgroundDrawable

 
fun View.setShowAlphaAnimation(duration: Short) {
    animation = AlphaAnimation(0f, 1f).apply { this.duration = duration }
}

fun View.setHideAlphaAnimation(duration: Short) {
    animation = AlphaAnimation(1f, 0f).apply { this.duration = duration }
}

fun View.setBackgroundColorWithRadius(color: Int, dpValue: Int) {
    if(Build.VERSION.SDK_INT >= 16) {
        background = GradientDrawable().apply {
            setColor(color)
            cornerRadius = context.convertDpToPixel(dpValue.toFloat())
        }
    } else {
        backgroundDrawable = GradientDrawable().apply {
            setColor(color)
            cornerRadius = context.convertDpToPixel(dpValue.toFloat())
        }
    }

}

fun View.setBackgroundColorOnAnimation(preColor: Int, postColor: Int) {
    val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), preColor, postColor)
            .apply {
                duration = 2000
                addUpdateListener {
                    setBackgroundColor(it.animatedValue as Int)
                }
            }
    colorAnimation.start()
}