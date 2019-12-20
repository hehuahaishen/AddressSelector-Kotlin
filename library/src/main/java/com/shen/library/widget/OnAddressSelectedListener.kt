package com.shen.library.widget

import com.shen.library.bean.City
import com.shen.library.bean.County
import com.shen.library.bean.Province
import com.shen.library.bean.Street

interface OnAddressSelectedListener {
    /**
     * 操作前请注意判空
     */
    fun onAddressSelected(province: Province?, city: City?, county: County?, street: Street?)
}
