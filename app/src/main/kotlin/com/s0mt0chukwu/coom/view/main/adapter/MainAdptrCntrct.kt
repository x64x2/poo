package com.pickth.Coom.view.main.adapter

import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.formats.NativeContentAdView
import com.s0mt0chukwu.coom.listener.OnCoomTL
import com.s0mt0chukwu.coom.listener.OnCoomDL
import com.s0mt0chukwu.coom.view.main.adapter.item.Coom

interface MainAdapterContract {
    interface View {
        fun setOnCoomCL(listener: OnCoomTL)
        fun setOnCoomDL(listener: OnCoomDL)
        fun setAdBuilder(builder: AdLoader.Builder)
    }

    interface Model {
        fun getItemCount(): Int
        fun getCoomItemCount(): Int
        fun addItem(item: Coom)
        fun addItem(item: Coom, position: Int)
        fun addItems(list: ArrayList<Coom>)
        fun getItem(position: Int): Coom
        fun getAllItems(): ArrayList<Coom>
        fun getCoomItems(): ArrayList<Coom>
        fun removeItem(position: Int): Boolean
        fun removeAllItems()
        fun changeItem(position: Int, Coom: Coom)
        fun swapItem(startPosition: Int, endPosition: Int)
        fun notifyChanged(position: Int)
        fun isExistPlus(): Boolean
        fun getIsUsedAd(): Boolean
    }
}