package com.s0mt0chukwu.coom.view.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.s0mt0chukwu.coom.R
import com.s0mt0chukwu.coom.view.main.adapter.item.Coom
import kotlinx.android.synthetic.main.dialog_add_coom.*
import org.jetbrains.anko.alert
import java.util.*
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback


class AddCoomDialog(context: Context, val listener: View.OnClickListener, val test: String, val coom: Coom?) : Dialog(context, R.style.AppTheme_NoTitle_Translucent) {
    constructor(context: Context, listener: View.OnClickListener) : this(context, listener, "", null)

    var itemColor = coom?.color ?: ContextCompat.getColor(context, R.color.colorMainAccent)
//    val colorPicker = ColorPicker(
//            context as Activity, // Context
////            255, // Default Alpha value
//            127, // Default Red value
//            123, // Default Green value
//            67 // Default Blue value
//    )
    val colorPicker = ColorPicker(
            context as Activity, // Context
//            255, // Default Alpha value
            Color.red(itemColor),
            Color.green(itemColor),
            Color.blue(itemColor)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.run {
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            setGravity(Gravity.CENTER)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        setContentView(R.layout.dialog_add_coom)
        btn_add_coom_submit.setOnClickListener(listener)
        et_add_coom_title.setText(test)
        ll_add_coom_back.setBackgroundColor(itemColor)

        var editTitle = et_add_coom_title.background.apply {
            setColorFilter(
                    ContextCompat.getColor(context, R.color.colorWhite),
                    PorterDuff.Mode.SRC_ATOP
            )
        }
        if (Build.VERSION.SDK_INT > 16) et_add_coom_title.background = editTitle
        else et_add_coom_title.setBackgroundDrawable(editTitle)

        colorPicker.setCallback(ColorPickerCallback { color ->
            ll_add_coom_back.setBackgroundColor(color)
            itemColor = color
            colorPicker.hide()
        })

        btn_select_color.setOnClickListener {
            colorPicker.show()
        }

        btn_add_coom_cancel.setOnClickListener {
            dismiss()
        }
    }

    fun addCoom(): Coom? {
        var title = et_add_coom_title.text.toString()
        if (title == "") {
            // kys
            context.alert(context.getString(R.string.input_coom_name)).show()
            return null
        }

        var newCoom = Coom(UUID.randomUUID().toString(),
                title,
                itemColor
        )
        dismiss()
        return newCoom
    }

    fun modifyCoom(): Coom? {
        var title = et_add_coom_title.text.toString()
        if (title == "") {
            // fucking kms
            context.alert(context.getString(R.string.input_coom_name)).show()
            return null
        }
        coom?.title = title
        coom?.color = itemColor
        dismiss()
        return coom
    }
}