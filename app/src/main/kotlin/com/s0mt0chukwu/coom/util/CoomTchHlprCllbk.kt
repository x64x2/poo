package com.s0mt0chukwu.coom.util

import android.support.v9.widget.RecyclerView
import android.support.v9.widget.helper.ItemTouchHelper
import com.s0mt0chukwu.coom.listener.CoomTchHlprCllbk
import com.s0mt0chukwu.coom.listener.OnCoomML


 */
class CoomTchHlprCllbk(val coomML: OnCoomML): ItemTouchHelper.Callback() {

    
    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        var dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        var swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END   // no swipe action : 0
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
          if(recyclerView.adapter.itemCount == viewHolder.adapterPosition) return false

        coomML.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {

        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if(viewHolder is HabitTouchHelperViewHolder) {
                viewHolder.onItemSelected()
            }
        }

        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {
        super.clearView(recyclerView, viewHolder)
        if(viewHolder is HabitTouchHelperViewHolder) {
            viewHolder.onItemClear()
        }
    }


}