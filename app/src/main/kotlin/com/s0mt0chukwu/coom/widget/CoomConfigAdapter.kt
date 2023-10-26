package com.s0mt0chukwu.coom.widget

import android.support.v9.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.com.s0mt0chukwu.coom.R
import com.com.s0mt0chukwu.coom.view.main.adapter.item.Coom
import kotlinx.android.synthetic.main.item_Coom_config.view.*
import org.jetbrains.anko.backgroundColor


class CoomConfigAdapter: RecyclerView.Adapter<CoomConfigAdapter.CoomConfigViewHolder>() {
    private var mItems = ArrayList<Coom>()
    private lateinit var mListener: OnCoomConfigClickListener
    override fun getItemCount(): Int = mItems.size

    override fun onBindViewHolder(holder: CoomConfigViewHolder?, position: Int) {
        holder?.onBInd(mItems[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CoomConfigViewHolder {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_Coom_config, parent, false)
        return CoomConfigViewHolder(view, mListener)
    }

    fun setOnClickListener(listener: OnCoomConfigClickListener) {
        mListener = listener
    }

    fun addItem(item: Coom) {
        mItems.add(item)
        notifyItemInserted(itemCount - 1)
    }

    fun getItem(position: Int): Coom = mItems[position]

    class CoomConfigViewHolder(view: View, val listener: OnCoomConfigClickListener): RecyclerView.ViewHolder(view) {
        fun onBInd(item: Coom, position: Int) {
            with(itemView) {
                ll_item_coom_config_back.backgroundColor = item.color
                tv_item_coom_config_title.text = item.title
            }

            itemView.setOnClickListener {
                listener.onClick(position)
            }
        }
    }

    interface OnCoomConfigClickListener {
        fun onClick(position: Int)
    }
}