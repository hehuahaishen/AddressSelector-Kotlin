package com.shen.library.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import com.shen.library.R
import com.shen.library.bean.City
import com.shen.library.bean.County
import com.shen.library.bean.Province
import com.shen.library.bean.Street
import com.shen.library.manager.AddressDictManager
import kotlin.concurrent.thread

/**
 * author:  shen
 * date:    2019/12/18
 *
 */
class AddressSelector(private val mContext: Context) {

    private var mTabIndex = INDEX_TAB_PROVINCE      //默认是省份
    private var mStopTabIndex = INDEX_TAB_STREET    //在哪个区域停止，不加载子数据

    private var mProvinceAdapter: AddressAdapter<Province>? = null
    private var mCityAdapter: AddressAdapter<City>? = null
    private var mCountyAdapter: AddressAdapter<County>? = null
    private var mStreetAdapter: AddressAdapter<Street>? = null

    /** 得到数据库管理者 */
    val mAddressDictManager: AddressDictManager

    private var mSelectedColor: Int = 0
    private var mUnSelectedColor: Int = 0

    /** 设置地址监听 */
    var onAddressSelectedListener: OnAddressSelectedListener? = null
    private var mDialogCloseListener: OnDialogCloseListener? = null
    private var mSelectorAreaPositionListener: onSelectorAreaPositionListener? = null

    private val mInflater: LayoutInflater
    /** 获得view */
    var mRootView: View? = null
        private set
    private var mIndicator: View? = null
    private var mLLayoutTab: LinearLayout? = null
    private var mTvProvince: TextView? = null
    private var mTvCity: TextView? = null
    private var mTvCounty: TextView? = null
    private var mTvStreet: TextView? = null
    private var mProgressBar: ProgressBar? = null
    private var mRv: UninterceptableRecyclerView? = null
    private var mIvClose: ImageView? = null

    var handler : Handler? = null

    init {
        mInflater = LayoutInflater.from(mContext)
        mAddressDictManager = AddressDictManager(mContext)
        initViews()
        initAdapters()
        initHandler()
        retrieveProvinces()
    }

    companion object {
        const val INDEX_TAB_PROVINCE = 0      //省份标志
        const val INDEX_TAB_CITY = 1          //城市标志
        const val INDEX_TAB_COUNTY = 2        //乡镇标志
        const val INDEX_TAB_STREET = 3        //街道标志

        private val WHAT_PROVINCES_PROVIDED = 0
        private val WHAT_CITIES_PROVIDED = 1
        private val WHAT_COUNTIES_PROVIDED = 2
        private val WHAT_STREETS_PROVIDED = 3
    }

    /**
     * 初始化布局
     */
    private fun initViews() {
        mRootView = mInflater.inflate(R.layout.address_selector, null)
        mRootView!!.run {

            mIvClose = findViewById(R.id.iv_close)

            mLLayoutTab = findViewById(R.id.layout_tab)
            mTvProvince = findViewById(R.id.textViewProvince)       //省份
            mTvCity = findViewById(R.id.textViewCity)               //城市
            mTvCounty = findViewById(R.id.textViewCounty)           //区 乡镇
            mTvStreet = findViewById(R.id.textViewStreet)           //街道
            mIndicator = findViewById(R.id.indicator)               //指示器

            mRv = findViewById(R.id.rv)

            mProgressBar = findViewById(R.id.progressBar)           //进度条
        }

        mTvProvince!!.setOnClickListener { tabClick(mProvinceAdapter!!, INDEX_TAB_PROVINCE) }
        mTvCity!!.setOnClickListener{ tabClick(mCityAdapter!!, INDEX_TAB_CITY) }
        mTvCounty!!.setOnClickListener{ tabClick(mCountyAdapter!!, INDEX_TAB_COUNTY) }
        mTvStreet!!.setOnClickListener{ tabClick(mStreetAdapter!!, INDEX_TAB_STREET) }
        mIvClose!!.setOnClickListener{ mDialogCloseListener?.dialogclose() }

        updateIndicator()
        val manager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false)
        mRv!!.layoutManager = manager
    }

    /**
     * 在哪个区域停止，不加载子数据
     */
    fun setStopTabIndex(@IntRange(from = 0, to = 3) stopTabIndex : Int) {
        mStopTabIndex = stopTabIndex
    }


    /**
     * 设置tab字体选中的颜色
     */
    fun isShowImageViewClose(isShow : Boolean) {
        mIvClose!!.visibility = if(isShow) View.VISIBLE else View.INVISIBLE
    }

    /**
     * 设置tab字体选中的颜色
     */
    fun setTextSelectedColor(selectedColor: Int) {
        mSelectedColor = selectedColor
    }

    /**
     * 设置tab字体没有选中的颜色
     */
    fun setTextUnSelectedColor(unSelectedColor: Int) {
        mUnSelectedColor = unSelectedColor
    }

    /**
     * 设置字体的大小
     */
    fun setTextSize(dp: Float) {
        mTvProvince!!.textSize = dp
        mTvCity!!.textSize = dp
        mTvCounty!!.textSize = dp
        mTvStreet!!.textSize = dp
    }

    /**
     * 设置字体的背景
     */
    fun setBackgroundColor(colorId: Int) {
        mLLayoutTab!!.setBackgroundColor(ContextCompat.getColor(mContext, colorId))
    }

    /**
     * 设置指示器的背景
     */
    fun setIndicatorBackgroundColor(colorId: Int) {
        mIndicator!!.setBackgroundColor(ContextCompat.getColor(mContext, colorId))
    }

    /**
     * 设置指示器的背景
     */
    fun setIndicatorBackgroundColor(color: String) {
        mIndicator!!.setBackgroundColor(Color.parseColor(color))
    }


    /**
     * 更新tab 指示器
     */
    private fun updateIndicator() {
        mRootView!!.post {
            when (mTabIndex) {
                INDEX_TAB_PROVINCE //省份
                -> buildIndicatorAnimatorTowards(mTvProvince!!).start()
                INDEX_TAB_CITY //城市
                -> buildIndicatorAnimatorTowards(mTvCity!!).start()
                INDEX_TAB_COUNTY //乡镇
                -> buildIndicatorAnimatorTowards(mTvCounty!!).start()
                INDEX_TAB_STREET //街道
                -> buildIndicatorAnimatorTowards(mTvStreet!!).start()
            }
        }
    }

    /**
     * tab 来回切换的动画
     *
     * @param tab
     * @return
     */
    @SuppressLint("ObjectAnimatorBinding")
    private fun buildIndicatorAnimatorTowards(tab: TextView): AnimatorSet {
        // mIndicator 水平移动（x）  从  mIndicator!!.x 移动到 tab.x
        val xAnimator = ObjectAnimator.ofFloat(mIndicator, "X", mIndicator!!.x, tab.x)

        val params = mIndicator!!.layoutParams
        // 从 params.width(这么宽) 变到 tab.measuredWidth(这么宽)
        val widthAnimator = ValueAnimator.ofInt(params.width, tab.measuredWidth)
        widthAnimator.addUpdateListener { animation ->
            params.width = animation.animatedValue as Int  // 最新的值
            mIndicator!!.layoutParams = params
        }

        val set = AnimatorSet()
        set.interpolator = FastOutSlowInInterpolator()
        set.playTogether(xAnimator, widthAnimator)

        return set
    }

    /**
     * 初始化adapter
     */
    private fun initAdapters() {
        mProvinceAdapter = AddressAdapter(mutableListOf())
        mCityAdapter = AddressAdapter(mutableListOf())
        mCountyAdapter = AddressAdapter(mutableListOf())
        mStreetAdapter = AddressAdapter(mutableListOf())
    }

    /**
     * handler 处理查询出来的数据
     */
    private fun initHandler() {
        handler = Handler(Handler.Callback { msg ->
            when (msg.what) {
                WHAT_PROVINCES_PROVIDED                         // 更新省份列表
                -> setQueryDataProvince(msg.obj as MutableList<Province>)
                WHAT_CITIES_PROVIDED                            // 更新城市列表
                -> setQueryData(mCityAdapter!!, msg.obj as MutableList<City>, INDEX_TAB_CITY)
                WHAT_COUNTIES_PROVIDED                          // 更新乡镇列表
                -> setQueryData(mCountyAdapter!!, msg.obj as MutableList<County>, INDEX_TAB_COUNTY)
                WHAT_STREETS_PROVIDED                           // 更新街道列表
                -> setQueryData(mStreetAdapter!!, msg.obj as MutableList<Street>, INDEX_TAB_STREET)
            }

            updateTabsVisibility()
            updateProgressVisibility()
            updateIndicator()

            true
        })
    }

    /**
     * 将"省"数据，添加到适配器
     */
    private fun setQueryDataProvince(list : MutableList<Province>){
        mProvinceAdapter!!.setList(list)
        mProvinceAdapter!!.mOnItemClickListener = object : AddressAdapter.OnItemClickListener{
            override fun itemClick(view: View, position: Int) {
                onItemClick(position)
            }
        }
        mRv!!.adapter = mProvinceAdapter
    }

    /**
     * 将查出的数据，添加到适配器
     */
    private fun <T> setQueryData(adapter: AddressAdapter<T>, list : MutableList<T>, index_tab : Int){
        adapter.setList(list)
        adapter.mOnItemClickListener = object : AddressAdapter.OnItemClickListener{
            override fun itemClick(view: View, position: Int) {
                onItemClick(position)
            }
        }
        if (adapter.listBean.isNotEmpty()) {
            mRv!!.adapter = adapter                 // 以次级内容更新列表
            mTabIndex = index_tab                   // 更新索引为次级
        } else {
            callbackInternal()                      // 次级无内容，回调
        }
    }

    /**
     * 点击 tab 的操作
     */
    private fun tabClick(adapter: AddressAdapter<*>, index_tab: Int){
        mTabIndex = index_tab
        mRv!!.adapter = adapter
        if (!adapter.isOutOfBounds()) {
            mRv!!.run {
                layoutManager?.scrollToPosition(adapter.mIndex)
            }
        }
        updateTabsVisibility()
        updateIndicator()
    }

    /**
     * 更新tab显示
     */
    private fun updateTabsVisibility() {
        setViewVisibilityEnabled(mTvProvince!!, mProvinceAdapter!!, INDEX_TAB_PROVINCE)
        setViewVisibilityEnabled(mTvCity!!, mCityAdapter!!, INDEX_TAB_CITY)
        setViewVisibilityEnabled(mTvCounty!!, mCountyAdapter!!, INDEX_TAB_COUNTY)
        setViewVisibilityEnabled(mTvStreet!!, mStreetAdapter!!, INDEX_TAB_STREET)

        if (mSelectedColor != 0 && mUnSelectedColor != 0) {
            updateTabTextColor()
        }
    }

    /**
     * 更新字体的颜色
     */
    private fun updateTabTextColor() {
        setTabTextColor(mTvProvince!!, INDEX_TAB_PROVINCE)
        setTabTextColor(mTvCity!!, INDEX_TAB_CITY)
        setTabTextColor(mTvCounty!!, INDEX_TAB_COUNTY)
        setTabTextColor(mTvStreet!!, INDEX_TAB_STREET)
    }

    private fun setViewVisibilityEnabled(view : View, adapter : AddressAdapter<*>, index_tab: Int){
        view.run {
            visibility = if(adapter.listBean.isNotEmpty()) View.VISIBLE else View.GONE
            isEnabled = mTabIndex != index_tab
        }
    }

    private fun setTabTextColor(view : TextView, index_tab: Int){
        if (mTabIndex != index_tab) {
            view.setTextColor(ContextCompat.getColor(mContext, mSelectedColor))
        } else {
            view.setTextColor(ContextCompat.getColor(mContext, mUnSelectedColor))
        }
    }

    fun onItemClick(position: Int) {
        when (mTabIndex) {
            INDEX_TAB_PROVINCE -> {
                val (id, code, name) = mProvinceAdapter!!.listBean[position]
                setTextView(mTvProvince!!, name, mTvCity!!, mTvCounty!!, mTvStreet!!)
                cleanList(mCityAdapter!!, mCountyAdapter!!, mStreetAdapter!!)
                mProvinceAdapter!!setSelect(position)   // 更新已选中项
                if(mStopTabIndex > INDEX_TAB_PROVINCE)
                    retrieveCitiesWith(id)                  // 根据省份的id,从数据库中查询城市列表
                else
                    callBackInternalAndUpdateUI()
            }
            INDEX_TAB_CITY -> {
                val (id, code, name) = mCityAdapter!!.listBean[position]
                setTextView(mTvCity!!, name, mTvCounty!!, mTvStreet!!)
                cleanList(mCountyAdapter!!, mStreetAdapter!!)
                mCityAdapter!!setSelect(position)       // 更新已选中
                if(mStopTabIndex > INDEX_TAB_CITY)
                    retrieveCountiesWith(id)                // 根据城市的id,从数据库中查询城市列表
                else
                    callBackInternalAndUpdateUI()
            }
            INDEX_TAB_COUNTY -> {
                val (id, code, name) = mCountyAdapter!!.listBean[position]
                setTextView(mTvCounty!!, name, mTvStreet!!)
                cleanList(mStreetAdapter!!)
                mCountyAdapter!!setSelect(position)
                if(mStopTabIndex > INDEX_TAB_COUNTY)
                    retrieveStreetsWith(id)
                else
                    callBackInternalAndUpdateUI()
            }
            INDEX_TAB_STREET -> {
                val (id, code, name) = mStreetAdapter!!.listBean[position]
                setTextView(mTvStreet!!, name)
                mStreetAdapter!!setSelect(position)

                callBackInternalAndUpdateUI()
            }
        }
    }

    private fun callBackInternalAndUpdateUI(){
        callbackInternal()
        updateTabsVisibility()
        updateProgressVisibility()
        updateIndicator()
    }

    /**
     * 选中项的区域名，放到对应的 tab 上， 其子级tab设为"请选择"
     */
    private fun setTextView(selectorView : TextView, selectorAreaName : String, vararg views: TextView){
        selectorView.text = selectorAreaName
        for (view in views){
            view.text = "请选择"
        }
    }

    /**
     * 清空子级数据
     */
    private fun cleanList(vararg adapters: AddressAdapter<*>){
        for (adapter in adapters){
            adapter.cleanList()
        }
    }


    /**
     * 查询省份列表
     */
    private fun retrieveProvinces() {
        mProgressBar!!.visibility = View.VISIBLE
        val provinceList = mAddressDictManager.provinceList
        handler!!.sendMessage(Message.obtain(handler, WHAT_PROVINCES_PROVIDED, provinceList))
    }

    /**
     * 根据省份id查询城市列表
     * @param provinceId  城市id
     */
    private fun retrieveCitiesWith(provinceId: Int) {
        mProgressBar!!.visibility = View.VISIBLE
        thread(start = true) {
            val cityList = mAddressDictManager.getCityList(provinceId)
            handler!!.sendMessage(Message.obtain(handler, WHAT_CITIES_PROVIDED, cityList))
        }
    }

    /**
     * 根据城市id查询乡镇列表
     * @param cityId 乡镇id
     */
    private fun retrieveCountiesWith(cityId: Int) {
        mProgressBar!!.visibility = View.VISIBLE
        val countyList = mAddressDictManager.getCountyList(cityId)
        handler!!.sendMessage(Message.obtain(handler, WHAT_COUNTIES_PROVIDED, countyList))
    }

    /**
     * 根据乡镇id查询乡镇列表
     * @param countyId 街道id
     */
    private fun retrieveStreetsWith(countyId: Int) {
        mProgressBar!!.visibility = View.VISIBLE
        val streetList = mAddressDictManager.getStreetList(countyId)
        handler!!.sendMessage(Message.obtain(handler, WHAT_STREETS_PROVIDED, streetList))
    }

    /**
     * 省份 城市 乡镇 街道 都选中完 后的回调
     */
    private fun callbackInternal() {
        onAddressSelectedListener?.run {
            onAddressSelected(mProvinceAdapter!!.getSelectItem(),
                mCityAdapter!!.getSelectItem(),
                mCountyAdapter!!.getSelectItem(),
                mStreetAdapter!!.getSelectItem())
        }

        mSelectorAreaPositionListener?.run {
            selectorAreaPosition(mProvinceAdapter!!.mIndex,
                mProvinceAdapter!!.mIndex,
                mProvinceAdapter!!.mIndex,
                mProvinceAdapter!!.mIndex)
        }
    }

    /**
     * 更新进度条
     */
    private fun updateProgressVisibility() {
        val itemCount = mRv!!.adapter!!.itemCount
        mProgressBar!!.visibility = if (itemCount > 0) View.GONE else View.VISIBLE
    }


    interface OnDialogCloseListener {
        fun dialogclose()
    }

    fun setOnDialogCloseListener(listener: OnDialogCloseListener) {
        mDialogCloseListener = listener
    }

    interface onSelectorAreaPositionListener {
        fun selectorAreaPosition(provincePosition: Int, cityPosition: Int,
            countyPosition: Int, streetPosition: Int)
    }

    fun setOnSelectorAreaPositionListener(listener: onSelectorAreaPositionListener) {
        mSelectorAreaPositionListener = listener
    }


    /**
     * 根据code 来显示选择过的地区
     */
    fun getSelectedArea(provinceCode: String, provincePosition: Int,
        cityCode: String, cityPosition: Int,
        countyCode: String, countyPosition: Int,
        streetCode: String, streetPosition: Int) {
//        LogUtil.d("数据", "getSelectedArea省份id=$provinceCode")
//        LogUtil.d("数据", "getSelectedArea城市id=$cityCode")
//        LogUtil.d("数据", "getSelectedArea乡镇id=$countyCode")
//        LogUtil.d("数据", "getSelectedArea 街道id=$streetCode")
        if (provinceCode.isNotEmpty()) {
            val province = mAddressDictManager.getProvinceBean(provinceCode)
            mTvProvince!!.text = province!!.name
            retrieveCitiesWith(province.id)
            mCityAdapter!!.cleanList()                    // 清空子级数据
            mCountyAdapter!!.cleanList()
            mStreetAdapter!!.cleanList()
            mProvinceAdapter!!setSelect(provincePosition) // 更新已选中项
        }
        if (cityCode.isNotEmpty()) {
            val city = mAddressDictManager.getCityBean(cityCode)
            mTvCity!!.text = city!!.name
            retrieveCountiesWith(city.id)
            mCountyAdapter!!.cleanList()
            mStreetAdapter!!.cleanList()
            mProvinceAdapter!!setSelect(cityPosition)

        }
        if (countyCode.isNotEmpty()) {
            val county = mAddressDictManager.getCountyBean(countyCode)
            mTvCounty!!.text = county!!.name
            retrieveStreetsWith(county.id)
            mStreetAdapter!!.cleanList()
            mProvinceAdapter!!setSelect(countyPosition)
        }
        if (streetCode.isNotEmpty()) {
            val street = mAddressDictManager.getStreetBean(streetCode)
            mTvStreet!!.text = street!!.name
            mStreetAdapter!!.notifyDataSetChanged()
            mProvinceAdapter!!setSelect(streetPosition)
        }

        callbackInternal()
    }
}