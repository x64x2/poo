package com.s0mt0chukwu.coom.extensions

import android.content.Context

fun Context.convertPixelToDp(px: Float): Float = px / resources.displayMetrics.density

fun Context.convertPixelToDp(px: Int): Int = convertPixelToDp(px.toFloat()).toInt()

fun Context.convertDpToPixel(dp: Float): Float = dp * resources.displayMetrics.density

fun Context.convertDpToPixel(dp: Int): Int = convertDpToPixel(dp.toFloat()).toInt()