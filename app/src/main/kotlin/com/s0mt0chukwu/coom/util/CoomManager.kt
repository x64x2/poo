package com.s0mt0chukwu.coom.util

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.s0mt0chukwu.coom.view.main.adapter.item.Coom
import java.util.*


object CoomManager {
    private var mCoom = ArrayList<Coom>()
    val TAG = "${javaClass.simpleName}"

    fun getCoom(context: Context): ArrayList<Coom> {
        if(mCoom.size == 0) {
            val json = context
                    .getSharedPreferences("Coom", 0)
                    .getString("Coom", "")

            if(json == "") return mCoom

            val type = object: TypeToken<ArrayList<Coom>>(){}.type
            mCoom = Gson().fromJson<ArrayList<Coom>>(json, type)
        }

        return mCoom
    }

    fun notifyDataSetChanged(context: Context) {
        context.getSharedPreferences("Coom", 0)
                .edit()
                .putString("Coom", Gson().toJson(mCoom).toString())
                .apply()
    }

    fun notifyDataSetChanged(context: Context, Coom: ArrayList<Coom>) {
        mCoom = Coom
        context.getSharedPreferences("Coom", 0)
                .edit()
                .putString("Coom", Gson().toJson(mCoom).toString())
                .apply()
    }

    fun addCoom(context: Context, Coom: Coom) {
        getCoom(context).add(Coom)
        notifyDataSetChanged(context)
    }

    fun removeCoom(context: Context, position: Int) {
        getCoom(context).removeAt(position)
        notifyDataSetChanged(context)
    }

    fun swapCoom(context: Context, startPosition: Int, endPosition: Int) {
        Collections.swap(getCoom(context), startPosition, endPosition)
        notifyDataSetChanged(context)
    }

    fun removeAllCoom(context: Context) {
        getCoom(context).clear()
        notifyDataSetChanged(context)
    }

    fun logCoomtatus(context: Context) {
        for(i in getCoom(context)) {
            Log.v(TAG, i.toString())
        }
    }
}