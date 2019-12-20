package com.shen.library.widget

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.WindowManager
import androidx.annotation.IntRange

import com.shen.library.R


/**
 * Created by smartTop on 2016/10/19.
 */

class BottomDialog : Dialog {
    private var mSelector: AddressSelector? = null

    constructor(context: Context, dp : Float = Companion.Window_Height_Dp) : super(context, R.style.bottom_dialog) {
        init(context, dp)
    }

    constructor(context: Context, themeResId: Int, dp : Float = Companion.Window_Height_Dp) : super(context, themeResId) {
        init(context, dp)
    }

    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener,
        dp : Float = Companion.Window_Height_Dp
    ) : super(context, cancelable, cancelListener) {
        init(context, dp)
    }

    /**
     * 初始化
     *
     * @param context
     */
    private fun init(context: Context, dp : Float) {
        mSelector = AddressSelector(context)
        mSelector!!.isShowImageViewClose(true)
        setContentView(mSelector!!.mRootView!!)

        val window = window
        val params = window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = dp2px(context, dp)
        window.attributes = params
        window.setGravity(Gravity.BOTTOM)
    }

    companion object {
        fun show(context: Context, listener: OnAddressSelectedListener? = null): BottomDialog {
            val dialog = BottomDialog(context, R.style.bottom_dialog)
            dialog.mSelector!!.onAddressSelectedListener = listener
            dialog.show()

            return dialog
        }

        const val Window_Height_Dp = 300f
    }

    fun dp2px(context: Context, dp: Float): Int {
        return Math.ceil((context.resources.displayMetrics.density * dp).toDouble()).toInt()
    }

    fun setOnAddressSelectedListener(listener: OnAddressSelectedListener) {
        mSelector!!.onAddressSelectedListener = listener
    }

    fun setDialogDismisListener(listener: AddressSelector.OnDialogCloseListener) {
        mSelector!!.setOnDialogCloseListener(listener)
    }

    /**
     * 设置选中位置的监听
     * @param listener
     */
    fun setSelectorAreaPositionListener(listener: AddressSelector.onSelectorAreaPositionListener) {
        mSelector!!.setOnSelectorAreaPositionListener(listener)
    }

    /**
     * 设置字体选中的颜色
     */
    fun setTextSelectedColor(selectedColor: Int) {
        mSelector!!.setTextSelectedColor(selectedColor)
    }

    /**
     * 设置字体没有选中的颜色
     */
    fun setTextUnSelectedColor(unSelectedColor: Int) {
        mSelector!!.setTextUnSelectedColor(unSelectedColor)
    }

    /**
     * 设置字体的大小
     */
    fun setTextSize(dp: Float) {
        mSelector!!.setTextSize(dp)
    }

    /**
     * 设置字体的背景
     */
    fun setBackgroundColor(colorId: Int) {
        mSelector!!.setBackgroundColor(colorId)
    }

    /**
     * 设置指示器的背景
     */
    fun setIndicatorBackgroundColor(colorId: Int) {
        mSelector!!.setIndicatorBackgroundColor(colorId)
    }

    /**
     * 设置指示器的背景
     */
    fun setIndicatorBackgroundColor(color: String) {
        mSelector!!.setIndicatorBackgroundColor(color)
    }

    /**
     * 设置指示器的背景
     */
    fun setStopTabIndex(@IntRange(from = 0, to = 3) stopTabIndex : Int) {
        mSelector!!.setStopTabIndex(stopTabIndex)
    }

    /**
     * 设置已选中的地区
     * @param provinceCode 省份code
     * @param provinPosition 省份所在的位置
     * @param cityCode   城市code
     * @param cityPosition  城市所在的位置
     * @param countyCode     乡镇code
     * @param countyPosition  乡镇所在的位置
     * @param streetCode      街道code
     * @param streetPosition  街道所在位置
     */
    fun setDisplaySelectorArea(provinceCode: String, provinPosition: Int,
        cityCode: String, cityPosition: Int,
        countyCode: String, countyPosition: Int,
        streetCode: String, streetPosition: Int) {
        mSelector!!.getSelectedArea(provinceCode, provinPosition,
            cityCode, cityPosition,
            countyCode, countyPosition,
            streetCode, streetPosition)
    }
}
