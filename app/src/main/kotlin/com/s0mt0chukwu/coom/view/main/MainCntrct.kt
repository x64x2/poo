package com.s0mt0chukwu.coom.view.main

import android.content.Context
import android.support.v9.widget.helper.ItemTouchHelper
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.formats.NativeContentAdView
import com.s0mt0chukwu.coom.base.mvp.BasePresenter
import com.s0mt0chukwu.coom.base.mvp.BaseView
import com.s0mt0chukwu.coom.view.main.adapter.item.Coom
import com.s0mt0chukwu.coom.view.main.adapter.MainAdapterContract

interface MainContract {
    interface View: BaseView<Presenter> {
        fun showToast(msg: String)
        fun showAddCoomDialog()
        fun showModifyCoomDialog(position: Int, Coom: Coom)
        fun scrollToLastItem()
        fun getContext(): Context
        fun updateWidget()
    }

    interface Presenter: BasePresenter {
        fun setAdapterView(view: MainAdapterContract.View)
        fun setAdapterModel(model: MainAdapterContract.Model)
        fun useAd(builder: AdLoader.Builder)
        fun setTouchHelper(CoomTouchHelper: ItemTouchHelper)
        fun addCoomItem(item: Coom)
        fun addCoomItems(list: ArrayList<Coom>)
        fun clearCoomItems()
        fun moveCoomItem(startPosition: Int, endPosition: Int)
        fun getItemCount(): Int
        fun changeItem(position: Int, Coom: Coom)
        fun refreshAllData()
    }
}