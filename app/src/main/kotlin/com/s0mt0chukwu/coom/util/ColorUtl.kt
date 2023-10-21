package com.pickth.habit.util

import android.annotation.SuppressLint
import android.graphics.Color

object ColorUtil {
    val BLACK = 1
    val WHITE = 0

    @SuppressLint("Range")
    fun getContrastColor(color: Int): Int {
        val y = (299 * Color.red(color) + 587 * Color.green(color) + 114 * Color.blue(color)) / 1000
        return if (y >= 128) WHITE else BLACK
    }
}