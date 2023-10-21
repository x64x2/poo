package com.s0mt0chukwu.coom.base.activity

import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.AnkoLogger

/**
 * Created by coomking on 2022-08-10
 */

open class BaseActivity: AppCompatActivity(), AnkoLogger {
    val TAG = "${javaClass.simpleName}"
}