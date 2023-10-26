package com.s0mt0chukwu.coom.widget

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.support.v9.widget.LinearLayoutManager
import android.view.View
import android.widget.RemoteViews
import com.s0mt0chukwu.coom.R
import com.s0mt0chukwu.coom.base.activity.BaseActivity
import com.s0mt0chukwu.coom.util.CoomManager
import com.s0mt0chukwu.coom.util.StringUtl
import kotlinx.android.synthetic.main.activity_coom_config.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

class CoomConfigActivity: BaseActivity() {

    private var mAppWidgetId: Int = 0
    private lateinit var mAppWidgetManager: AppWidgetManager
    private lateinit var mRemoteView: RemoteViews

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coom_config)

        var mAdapter = coomConfigAdapter()
        mAdapter.setOnClickListener(object: coomConfigAdapter.OncoomConfigClickListener {
            override fun onClick(position: Int) {
                alert("${mAdapter.getItem(position).title} ${applicationContext.getString(R.string.check_coom_name)}"){
                    yesButton {
                        // init bind widget
                        var coom = coomManager.getcooms(applicationContext)[position]
                        coomWidgetManager.addWidget(applicationContext, mAppWidgetId, coom.id)

                        mRemoteView.setViewVisibility(R.id.pb_widget_loading, View.GONE)
                        mRemoteView.setInt(R.id.iv_widget_coom_background, "setColorFilter", coom.color)

                        // init bind view
                        mRemoteView.setTextViewText(R.id.tv_widget_coom_title, coom.title)
                        if(!coom.days.isEmpty()) {
                            if(coom.days[0] == StringUtil.getCurrentDay())  {
                                mRemoteView.setViewVisibility(R.id.iv_widget_coom_select, View.VISIBLE)
                            } else {
                                mRemoteView.setViewVisibility(R.id.iv_widget_coom_select, View.GONE)
                            }
                        } else {
                            mRemoteView.setViewVisibility(R.id.iv_widget_coom_select, View.GONE)
                        }

                        // init event 
                        var listener = PendingIntent.getBroadcast(
                                applicationContext,
                                0,
                                Intent(applicationContext, coomWidget::class.java).apply {
                                    action = coomWidget.ACTION_CLICKED + mAppWidgetId
                                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
                                },
                                PendingIntent.FLAG_UPDATE_CURRENT
                        )
                        mRemoteView.setOnClickPendingIntent(R.id.fl_widget_coom, listener)

                        // update view
                        mAppWidgetManager
                                .updateAppWidget(mAppWidgetId, mRemoteView)

                        // intent
                        var resultIntent = Intent()
                        resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                    noButton {  }
                }.show()

            }

        })
        rv_coom_config.run {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        for(item in coomManager.getcooms(this)) mAdapter.addItem(item)

        // added widget
        var mExtras = intent.extras
        if(mExtras != null) mAppWidgetId = mExtras.
                getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

        mAppWidgetManager = AppWidgetManager.getInstance(this)
        mRemoteView = RemoteViews(this.packageName, R.layout.widget_coom)
    }
}