package com.s0mt0chukwu.coom.util

import android.content.Context
import android.graphics.Rect
import android.support.v9.widget.RecyclerView
import android.view.View
import com.s0mt0chukwu.coom.extensions.convertDpToPixel

class GridSpacingItemDecoration(context: Context, val spanCount: Int, var spacing: Int, val includeEdge: Boolean): RecyclerView.ItemDecoration() {
    init {
        spacing = context.convertDpToPixel(spacing)
    }

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {

        val position = parent!!.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column

        if (includeEdge) {
            outRect!!.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = spacing
            }
            outRect.bottom = spacing // item bottom
        } else {
            outRect!!.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacing // item top
            }
        }
    }
}