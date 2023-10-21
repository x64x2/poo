package com.s0mt0chukwu.coom.extensions.listener

import package com.s0mt0chukwu.coom.view.main.adapter.item.viewholder.CoomViewHolder

interface OnCoomDL {
    fun onStartDrag(holder: CoomViewHolder)


    fun onUpdateItems()
}