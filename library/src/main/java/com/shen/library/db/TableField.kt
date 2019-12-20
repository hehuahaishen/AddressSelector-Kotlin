package com.shen.library.db

/**
 * 表的管理类
 */
object TableField {
    /* 表名 */
    val TABLE_ADDRESS_DICT = "address_dict"             // 地址库字典表

    /* 字段名 */
    val FIELD_ID = "id"                                 // 公用的id

    /* 地址库字典表的字段名 */
    val ADDRESS_DICT_FIELD_ID = "id"
    val ADDRESS_DICT_FIELD_PARENTID = "parentId"        // 父id，自关联id主键
    val ADDRESS_DICT_FIELD_CODE = "code"                // 地址编号
    val ADDRESS_DICT_FIELD_NAME = "name"                // 中文名

    // 创建地址库字典表sql语句
    val CREATE_ADDRESS_DICT_SQL = "create table " + TABLE_ADDRESS_DICT +
            "(" + ADDRESS_DICT_FIELD_ID + " integer not null," +
            ADDRESS_DICT_FIELD_PARENTID + " integer not null," +
            ADDRESS_DICT_FIELD_CODE + " text," +
            ADDRESS_DICT_FIELD_NAME + " text)"
}
