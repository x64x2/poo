package com.s0mt0chukwu.coom.view.main

import android.support.v9.widget.helper.ItemTouchHelper
import android.util.Log
import com.google.android.gms.ads.AdLoader
import com.google.gson.Gson
import com.s0mt0chukwu.coom.base.mvp.BaseView
import com.s0mt0chukwu.coom.util.CoomManager
import com.s0mt0chukwu.coom.listener.OnCoomTL
import com.s0mt0chukwu.coom.listener.OnCoomDL
import com.s0mt0chukwu.coom.util.StringUtl
import com.s0mt0chukwu.coom.view.main.adapter.item.Coom
import com.s0mt0chukwu.coom.view.main.adapter.MainAdptrCntrct
import com.s0mt0chukwu.coom.view.main.adapter.item.AdItem
import com.s0mt0chukwu.coom.view.main.adapter.item.PlusCoom
import com.s0mt0chukwu.coom.view.main.adapter.item.viewholder.CoomViewHolder

class MainPresenter: MainContract.Presenter, OnCoomTL, OnCoomDL {

    val TAG = "${javaClass.simpleName}"

    private lateinit var mView: MainContract.View
    private lateinit var mAdapterView: MainAdapterContract.View
    private lateinit var mAdapterModel: MainAdapterContract.Model
    private lateinit var mCoomTouchHelper: ItemTouchHelper

    override fun attachView(view: BaseView<*>) {
        mView = view as MainContract.View
    }

    override fun setAdapterView(view: MainAdapterContract.View) {
        mAdapterView = view
        mAdapterView.setOnCoomClickListener(this)
        mAdapterView.setOnCoomDragListener(this)
    }

    override fun setAdapterModel(model: MainAdapterContract.Model) {
        mAdapterModel = model
    }

    override fun useAd(builder: AdLoader.Builder) {
        mAdapterView.setAdBuilder(builder)
        mAdapterModel.addItem(AdItem(), mAdapterModel.getItemCount())
    }

    override fun setTouchHelper(CoomTouchHelper: ItemTouchHelper) {
        mCoomTouchHelper = CoomTouchHelper
    }

    override fun getItemCount(): Int = mAdapterModel.getCoomItemCount()

    override fun addCoomItem(item: Coom) {
        mAdapterModel.addItem(item)
        CoomManager.addCoom(mView.getContext(), item)
    }

    override fun addCoomItems(list: ArrayList<Coom>) {
        mAdapterModel.addItems(list)
    }

    override fun clearCoomItems() {
        mAdapterModel.removeAllItems()
        CoomManager.removeAllCoom(mView.getContext())
    }

    override fun moveCoomItem(startPosition: Int, endPosition: Int) {
        // hehe
        if(mAdapterModel.getCoomItemCount() <= endPosition) {
            return
        }

        mAdapterModel.swapItem(startPosition, endPosition)
        CoomManager.notifyDataSetChanged(mView.getContext(), mAdapterModel.getCoomItems())
        CoomManager.swapCoom(mView.getContext(), startPosition, endPosition)
    }

    override fun onItemCheck(position: Int) {
        mAdapterModel.notifyChanged(position)
        mAdapterModel.getItem(position).days.add(0, StringUtil.getCurrentDay())

        CoomManager.notifyDataSetChanged(mView.getContext())
        mView.updateWidget()
    }

    override fun onItemUnCheck(position: Int) {
        mAdapterModel.notifyChanged(position)

        if(mAdapterModel.getItem(position).days[0] == StringUtil.getCurrentDay()) {

        }

        mAdapterModel.getItem(position).days.removeAt(0)

        CoomManager.notifyDataSetChanged(mView.getContext())
        mView.updateWidget()
    }

    override fun onItemRemove(position: Int) {
        mAdapterModel.removeItem(position)
        CoomManager.removeCoom(mView.getContext(), position)
    }

    override fun onItemModify(position: Int, Coom: Coom) {
        mView.showModifyCoomDialog(position, Coom)
    }

    override fun onLastItemClick() {
        mView.showAddCoomDialog()
        mView.scrollToLastItem()
    }

    override fun changeItem(position: Int, Coom: Coom) {
        mAdapterModel.changeItem(position, Coom)
        CoomManager.notifyDataSetChanged(mView.getContext())
    }

    override fun refreshAllData() {
        Log.v(TAG, "refreshAllData")
        for(i in 0 until getItemCount() - 1) {
            mAdapterModel.notifyChanged(i)
        }

        mView.updateWidget()
    }

    fun getCoomsWithJson(): String = Gson()
            .toJson(
                    CoomManager.getCooms(mView.getContext())
            )
            .toString()

    override fun onStartDrag(holder: CoomViewHolder) {
        mCoomTouchHelper.startDrag(holder)
    }

    override fun onUpdateItems() {
        refreshAllData()
    }

    fun addPlusView() {
        mAdapterModel.addItem(PlusCoom(), mAdapterModel.getItemCount())
    }
}