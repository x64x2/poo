package com.s0mt0chukwu.coom.listener

import com.package com.s0mt0chukwu.coom.view.main.adapter.item.Coom

interface OnCoomTouchListener {
    fun onItemCheck(position: Int)
    fun onItemUnCheck(position: Int)
    fun onItemRemove(position: Int)
    fun onItemModify(position: Int, habit: Habit)
    fun onLastItemClick()
}