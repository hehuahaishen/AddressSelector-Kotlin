package com.shen.library.bean

import java.util.ArrayList

/**
 * author:  shen
 * date:    2019/12/18
 *
 */

/**
 * 省份的实体类
 */
data class Province(
    var id: Int,
    var code: String,
    var name: String
)

/**
 * 城市的实体类
 */
data class City(
    var id: Int,
    var code: String,
    var name: String
)

/**
 * 区 乡镇的实体类
 */
data class County(
    var id: Int,
    var code: String,
    var name: String
)

/**
 * 街道的实体类
 */
data class Street(
    var id: Int,
    var code: String,
    var name: String
)


/**
 * banner基类
 */
data class AdressBean(
    var changeCount: Int,
    var code: String,
    var message: String,
    var changeRecords : MutableList<ChangeRecordsBean>
)

data class ChangeRecordsBean(
    /*** id */
    var id: Int,
    /*** 父id */
    var parentId: Int,
    /*** 地址编号 */
    var code: String,
    /*** 中文名 */
    var name: String
)