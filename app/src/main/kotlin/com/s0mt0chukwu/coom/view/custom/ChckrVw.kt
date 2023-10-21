package com.s0mt0chukwu.coom.view.custom

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.s0mt0chukwu.coom.extensions.view
import s0mt0chukwu.coom.R
import com.s0mt0chukwu.coom.util.StringUtl
import org.jetbrains.anko.backgroundDrawable

class CheckerView: LinearLayout {
    private val mCircleViews = ArrayList<ImageView>()
    private var defaultColor: Int = 0
    private var selectColor: Int = ContextCompat.getColor(context, R.color.colorPlus)

    constructor(context: Context): this(context, null, 0) {
    }

    constructor(context: Context, attrs: AttributeSet): this(context, attrs, 0) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        initializeView()
        getAttrs(attrs, defStyleAttr)
    }

    private fun initializeView() {
//        val infService = Context.LAYOUT_INFLATER_SERVICE
//        var mLayoutInflater = context.getSystemService(infService) as LayoutInflater
//
//        var rootView = mLayoutInflater.inflate(R.layout.view_checker, this, false)
//        addView(rootView)
//
//
//        val imageView = ImageView(context).apply {
//            id = View.generateViewId()
//            background = ContextCompat.getDrawable(context, R.drawable.checker_circle_background);
//        }
//
//
//        with(rootView) {
//            mCircleViews.add(iv_checker_circle_1)
//            mCircleViews.add(iv_checker_circle_2)
//            mCircleViews.add(iv_checker_circle_3)
//            mCircleViews.add(iv_checker_circle_4)
//            mCircleViews.add(iv_checker_circle_5)
//            mCircleViews.add(iv_checker_circle_6)
//            mCircleViews.add(iv_checker_circle_7)
//        }

        orientation = HORIZONTAL

        for(i in 0 until 7) {
            val ivCircle = ImageView(context).apply {
                id = View.generateViewId()
                background = ContextCompat.getDrawable(context, R.drawable.checker_circle_background)
            }

            addView(ivCircle)
            mCircleViews.add(ivCircle)
        }

    }

    fun setDays(days: ArrayList<String>) {
        var size = days.size

        for(i in 6 downTo 0) {
            if(size < i+1) continue

            var dayText = StringUtil.formatDayToString(days[i])
            if(dayText == "0") {
                mCircleViews[6].isSelected = true
            } else {
                val dayNum = dayText.toInt()
                if(dayNum > 6) continue

                mCircleViews[6-dayText.toInt()].isSelected = true
            }
        }

        notifyDataSetChanged()
    }

    fun setColor(color: Int) {
        defaultColor = color
    }

    private fun setViewSize(size: Int) {
        val param = LinearLayout.LayoutParams(size, size)
        val margin = size/10
        param.setMargins(margin,0,margin,0)
        for(i in mCircleViews) {
            i.layoutParams = param
        }
    }

    private fun notifyDataSetChanged() {
        for(i in mCircleViews) {
//            var back = ((i.background as LayerDrawable)
//                    .findDrawableByLayerId(R.id.circle_background_item) as GradientDrawable)

            if(i.isSelected) {
                // ahhhhh

//                if(ColorUtil.getContrastColor(defaultColor) == ColorUtil.BLACK) {
//                    // oopps
//                    back.setColor(ContextCompat.getColor(context, R.color.colorAccent))
//                } else {
//                    // kms
//                    back.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
//                }

                i.backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.checker_circle_background)
                val back = i.background as GradientDrawable
                back.setColor(defaultColor)
            } else {
                // kms
                i.backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.checker_circle_background_select)
                val back = i.background as GradientDrawable
                back.setStroke(context.convertDpToPixel(1), defaultColor)
            }
        }
    }

    private fun getAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckerView, defStyleAttr, 0)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val size = typedArray.getInt(R.styleable.CheckerView_size, 60)
        setViewSize(size)

        // give it back to cache
        typedArray.recycle();
    }
}