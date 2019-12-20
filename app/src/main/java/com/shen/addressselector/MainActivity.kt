package com.shen.addressselector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.shen.library.bean.*
import com.shen.library.widget.AddressSelector
import com.shen.library.widget.BottomDialog
import com.shen.library.widget.OnAddressSelectedListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener{

    private var mDialog: BottomDialog? = null
    private var mDialogProvince: Province? = null
    private var mDialogCity: City? = null
    private var mDialogCounty: County? = null
    private var mDialogStreet: Street? = null

    private var mProvince: Province? = null
    private var mCity: City? = null
    private var mCounty: County? = null
    private var mStreet: Street? = null

    private var mProvincePosition: Int = -1
    private var mCityPosition: Int = -1
    private var mCountyPosition: Int = -1
    private var mStreetPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_selector_area.setOnClickListener(this)

        setAddressSelector()
    }


    fun setAddressSelector(){

        val selector = AddressSelector(this)
        selector.setTextSize(15f)//设置字体的大小
        // selector.setIndicatorBackgroundColor("#00ff00");
        selector.setIndicatorBackgroundColor(android.R.color.holo_red_dark) //设置指示器的颜色
        // selector.setBackgroundColor(android.R.color.holo_red_light);         //设置字体的背景
        selector.setTextSelectedColor(android.R.color.holo_red_dark)        //设置tab字体选择的颜色
        selector.setTextUnSelectedColor(android.R.color.black)        //设置tab字体没选择的颜色
        selector.setStopTabIndex(AddressSelector.INDEX_TAB_STREET)
        val changeRecordsBean = ChangeRecordsBean(35, 0, "", "测试省")
        selector.mAddressDictManager.insertAddress(changeRecordsBean)//对数据库里增加一个数据
        selector.onAddressSelectedListener = object : OnAddressSelectedListener {
            override fun onAddressSelected(province: Province?, city: City?, county: County?, street: Street?) {
                mProvince = province
                mCity = city
                mCounty = county
                mStreet = street
            }
        }

        content.addView(selector.mRootView)
    }

    fun setTextView(s : String){
        tv_selector_area.text = s
    }

    override fun onClick(view: View) {
        if (mDialog != null) {
            mDialog!!.show()
        } else {
            mDialog = BottomDialog(this)
            mDialog?.run{
                setOnAddressSelectedListener(object : OnAddressSelectedListener{
                    override fun onAddressSelected(province: Province?, city: City?, county: County?, street: Street?) {
                        val s = "${province?.name ?: ""}${city?.name ?: ""}${county?.name ?: ""}${street?.name ?: ""}"
                        setTextView(s)

                        mDialogProvince = province
                        mDialogCity = city
                        mDialogCounty = county
                        mDialogStreet = street

                        dismiss()
                    }
                })
                setDialogDismisListener(object : AddressSelector.OnDialogCloseListener{
                    override fun dialogclose() {
                        dismiss()
                    }
                })
                setTextSize(14f)    //设置字体的大小
                setIndicatorBackgroundColor(android.R.color.holo_orange_light)//设置指示器的颜色
                setTextSelectedColor(android.R.color.holo_orange_light)//设置字体获得焦点的颜色
                setTextUnSelectedColor(android.R.color.holo_blue_light)//设置字体没有获得焦点的颜色
                setDisplaySelectorArea(mDialogProvince?.code ?: "", mProvincePosition,
                    mDialogCity?.code ?: "", mCityPosition,
                    mDialogCounty?.code ?: "", mCountyPosition,
                    mDialogStreet?.code ?: "", mStreetPosition)//设置已选中的地区
                setStopTabIndex(AddressSelector.INDEX_TAB_STREET)
                setSelectorAreaPositionListener(object : AddressSelector.onSelectorAreaPositionListener{
                    override fun selectorAreaPosition(
                        provincePosition: Int, cityPosition: Int,
                        countyPosition: Int, streetPosition: Int) {
                        mProvincePosition = provincePosition
                        mCityPosition = cityPosition
                        mCountyPosition = countyPosition
                        mStreetPosition = streetPosition
                    }
                })
                show()   // 显示窗口
            }
        }
    }
}
