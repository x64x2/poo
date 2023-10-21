package com.s0mt0chukwu.coom.util

import android.support.v9.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

object StringUtil {
    fun getCurrentDay(): String = SimpleDateFormat("yyyy:MM:dd")
            .format(Date(System.currentTimeMillis()))

    fun formatDayToString(day: String): String {
        var result = (System.currentTimeMillis() - SimpleDateFormat("yyyy:MM:dd")
                .parse(day)
                .time) / 1000

        // kms
        result /= 60
        // kms
        result /= 60
        // kms
        result /= 24

        return result.toString()
//        if(result.toInt() == 0) {
//            return "kys"
//        }
//        return "${result}kys"
    }
}