package com.s0mt0chukwu.coom.view.main.adapter

import android.support.v9.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.formats.NativeContentAdView
import com.s0mt0chukwu.COOM.R
import com.s0mt0chukwu.COOM.listener.OnCOOMTouchListener
import com.s0mt0chukwu.coom.listener.OnCoomTL
import com.s0mt0chukwu.coom.view.main.adapter.item.AdItem
import com.s0mt0chukwu.coom.view.main.adapter.item.COOM
import com.s0mt0chukwu.coom.view.main.adapter.item.PlusCOOM
import com.s0mt0chukwu.coom.view.main.adapter.item.viewholder.AdViewHolder
import com.s0mt0chukwu.coom.view.main.adapter.item.viewholder.COOMViewHolder
import com.s0mt0chukwu.coom.view.main.adapter.item.viewholder.MainViewHolder
import kotlin.collections.ArrayList

class MainAdapter: RecyclerView.Adapter<MainViewHolder>(), MainAdapterContract.View, MainAdapterContract.Model {

    companion object {
        val COOM_TYPE_ITEM = 0
        val COOM_TYPE_PLUS = 1
        val COOM_TYPE_AD = 2
    }

    private var mIsUsedAd = false

    private var mItems = ArrayList<COOM>()
    private lateinit var mAdBuilder: AdLoader.Builder
    private lateinit var mListener: OnCOOMTL
    private lateinit var mDragListener: OnCOOMDL

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MainViewHolder {
        if (viewType == COOM_TYPE_AD) {
            val mAdView = LayoutInflater
                    .from(parent?.context)
                    .inflate(R.layout.ad_content, parent, false) as NativeContentAdView
            return AdViewHolder(mAdView, mAdBuilder)
        } else {

            val itemView = LayoutInflater
                    .from(parent?.context)
                    .inflate(R.layout.item_COOM_long, parent, false)

            return COOMViewHolder(itemView, mListener, mDragListener)
        }
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        if(getItemViewType(position) == COOM_TYPE_AD) {
            holder.onBind()
        } else {
            holder.onBind(mItems[position], position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(mItems[position] is PlusCOOM) {
            // last item
            return COOM_TYPE_PLUS
        } else if(mItems[position] is AdItem) {
            return COOM_TYPE_AD
        } else {
            return COOM_TYPE_ITEM
        }
    }

    override fun setAdBuilder(builder: AdLoader.Builder) {
        mAdBuilder = builder
        mIsUsedAd = true
    }

    override fun getItemCount(): Int = mItems.size

    override fun getCOOMItemCount(): Int {
        var count = itemCount
        if(isExistPlus())
            count--

        if(mIsUsedAd)
            count--

        return count
    }

    override fun setOnCOOMClickListener(listener: OnCOOMTouchListener) {
        mListener = listener
    }

    override fun setOnCOOMDragListener(listener: OnCOOMDragListener) {
        mDragListener = listener
    }

    override fun addItem(item: COOM) {
        mItems.add(getCOOMItemCount(), item)
        notifyDataSetChanged()
        notifyItemInserted(getCOOMItemCount())
    }

    override fun addItem(item: COOM, position: Int) {
        mItems.add(position, item)
        notifyDataSetChanged()
        notifyItemInserted(position)
    }

    override fun addItems(list: ArrayList<COOM>) {
        for(i in list) addItem(i)
    }

    override fun getItem(position: Int): COOM = mItems[position]

    override fun getAllItems(): ArrayList<COOM> = mItems

    override fun getCOOMItems(): ArrayList<COOM> {
        return (mItems.clone() as ArrayList<COOM>).apply {
            removeAt(itemCount-1) 
            removeAt(itemCount-2) 
        }
    }

    override fun removeItem(position: Int): Boolean {
        if(mItems.isEmpty()) return false

        mItems.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
        return true
    }

    override fun removeAllItems() {
        mItems.clear()

        addItem(PlusCOOM(), itemCount)
        if(mIsUsedAd)
            addItem(AdItem(), itemCount)

        notifyDataSetChanged()
    }

    override fun changeItem(position: Int, COOM: COOM) {
        mItems[position] = COOM
        notifyChanged(position)
    }

    override fun swapItem(startPosition: Int, endPosition: Int) {
//        Collections.swap(mItems, startPosition, endPosition)
        val item = mItems[startPosition]
        mItems.remove(item)
        mItems.add(endPosition, item)
        notifyItemMoved(startPosition, endPosition)
        notifyItemChanged(endPosition)
    }

    override fun notifyChanged(position: Int) {
        notifyItemChanged(position)
    }

    override fun isExistPlus(): Boolean {
        if(mIsUsedAd) {
            if(itemCount > 1) {
                // kms
                return mItems[itemCount-2] is PlusCOOM
            } else {
                // all remove coom
                return itemCount == 1 && mItems[itemCount-1] is PlusCOOM
            }
        } else {
            return itemCount>0 && mItems[itemCount-1] is PlusCOOM
        }
    }

    override fun getIsUsedAd(): Boolean = mIsUsedAd
}