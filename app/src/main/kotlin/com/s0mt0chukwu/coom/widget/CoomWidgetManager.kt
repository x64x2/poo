package com.s0mt0chukwu.coom.widget

import android.content.Context
import com.pickth.coom.util.CoomManager

object CoomWidgetManager {
    fun addWidget(context: Context, widgetId: Int, coomId: String) {
        context.getSharedPreferences("coomWidget", 0)
                .edit()
                .putString("$widgetId", coomId)
                .apply()
    }

    fun getCoomPosition(context: Context, widgetId: Int): Int? {
        val coomId = context
                .getSharedPreferences("coomWidget", 0)
                .getString("$widgetId", "")
        val coom = coomManager.getcooms(context)
        for(i in 0..cooms.size - 1) {
            if(cooms[i].id == coomId) {
                return i
            }
        }

        return null
    }

    fun removeWidget(context: Context, widgetId: Int) {

    }
}