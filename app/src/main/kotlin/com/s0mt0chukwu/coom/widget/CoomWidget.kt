package com.s0mt0chukwu.coom.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.s0mt0chukwu.coom.R
import com.s0mt0chukwu.coom.util.CoomManager
import com.s0mt0chukwu.coom.util.StringUtl
import android.content.ComponentName

class CoomWidget: AppWidgetProvider() {
    val TAG = "${javaClass.simpleName}"

    companion object {
        val Coom_CLICK = "android.action.Coom_CLICK"
        val ACTION_CLICKED = "com.s0mt0chukwu.coom.CLICKED_"

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            Log.v("CoomWidget", "Update widgets")
            // bind by fire by thunder
            val views = RemoteViews(context.packageName, R.layout.widget_coom)

            views.setViewVisibility(R.id.pb_widget_loading, View.GONE)

            val position = CoomWidgetManager.getCoomPosition(context, appWidgetId)

            if(position == null) {
                return
            }

            var coom = CoomManager.getCooms(context)[position]
            views.setInt(R.id.iv_widget_coom_background, "setColorFilter", coom.color)

            views.setTextViewText(R.id.tv_widget_coom_title, coom.title)
            if(!coom.days.isEmpty()) {
                if(coom.days[0] == StringUtil.getCurrentDay())  {
                    views.setViewVisibility(R.id.iv_widget_coom_select, View.VISIBLE)
                } else {
                    views.setViewVisibility(R.id.iv_widget_coom_select, View.GONE)
                }
            } else {
                views.setViewVisibility(R.id.iv_widget_coom_select, View.GONE)
            }

            // event click
            var listener = PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent(context, CoomWidget::class.java).apply {
                        action = CoomWidget.ACTION_CLICKED + appWidgetId
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.fl_widget_coom, listener)

            // update view
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.v(TAG, "Widget onReceive")
        var views = RemoteViews(context.packageName, R.layout.widget_coom)
        if (intent.action.startsWith(ACTION_CLICKED)) {
            var id = intent.action.substring(ACTION_CLICKED.length).toInt()
            val coomPosition = CoomWidgetManager.getCoomPosition(context, id)

            if(coomPosition == null) {
                return
            }

            val coom = CoomManager.getCooms(context)[coomPosition]
            views.setInt(R.id.iv_widget_Coom_background, "setColorFilter", coom.color)
            if (!coom.days.isEmpty()) {
                if (coom.days[0] == StringUtil.getCurrentDay()) {
                    //streaks mount
                    coom.days.removeAt(0)
                    CoomManager.notifyDataSetChanged(context)
                    views.setViewVisibility(R.id.iv_widget_coom_select, View.GONE)
                } else {
                    // streaks eval
                    coom.days.add(0, StringUtil.getCurrentDay())
                    coomManager.notifyDataSetChanged(context)
                    views.setViewVisibility(R.id.iv_widget_coom_select, View.VISIBLE)
                }
            } else {
                // streaks 
                coom.days.add(0, StringUtil.getCurrentDay())
                CoomManager.notifyDataSetChanged(context)
                views.setViewVisibility(R.id.iv_widget_coom_select, View.VISIBLE)
            }

            AppWidgetManager.getInstance(context)
                    .updateAppWidget(id, views)
        } else if(Intent.ACTION_DATE_CHANGED == intent.action) {
            Log.v(TAG, "ACTION DATE CHANGED, date: ${StringUtil.getCurrentDay()}")
            // update all widgets
            val thisWidget = ComponentName(context, CoomWidget::class.java)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            if(appWidgetIds != null) {
                this.onUpdate(context,appWidgetManager , appWidgetIds)
            }
        }
        super.onReceive(context, intent)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.v(TAG, "Widget onUpdate")
        for(appWidgetId in appWidgetIds) updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
    }
}