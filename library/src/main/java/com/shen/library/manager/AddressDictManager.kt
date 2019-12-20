package com.shen.library.manager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.shen.library.bean.*

import com.shen.library.db.AssetsDatabaseManager
import com.shen.library.db.TableField
import com.shen.library.db.TableField.ADDRESS_DICT_FIELD_CODE
import com.shen.library.db.TableField.ADDRESS_DICT_FIELD_ID
import com.shen.library.db.TableField.ADDRESS_DICT_FIELD_NAME
import com.shen.library.db.TableField.ADDRESS_DICT_FIELD_PARENTID
import com.shen.library.db.TableField.FIELD_ID
import com.shen.library.db.TableField.TABLE_ADDRESS_DICT
import java.util.ArrayList

/**
 * 对地址库的增删改查
 */
class AddressDictManager(context: Context) {

    private val db: SQLiteDatabase?

    init {
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        val mg = AssetsDatabaseManager.getInstance(context)
        // 通过管理对象获取数据库
        db = mg.getDatabase("address.db")
    }



    /**
     * 查找--所有地址数据
     * @return
     */
    val addressList: MutableList<ChangeRecordsBean>
        get() {
            val list = mutableListOf<ChangeRecordsBean>()
            val cursor = db?.rawQuery("select * from $TABLE_ADDRESS_DICT order by sort asc",
                    null)
            cursor?.run {
                while (moveToNext()) {
                    val addressInfo = ChangeRecordsBean(
                        getInt(getColumnIndex(ADDRESS_DICT_FIELD_ID)),
                        getInt(getColumnIndex(ADDRESS_DICT_FIELD_PARENTID)),
                        getString(getColumnIndex(ADDRESS_DICT_FIELD_CODE)),
                        getString(getColumnIndex(ADDRESS_DICT_FIELD_NAME))
                    )
                    list.add(addressInfo)
                }
                cursor.close()
            }
            return list
        }

    /**
     * 查找--所有省份
     * @return
     */
    val provinceList: MutableList<Province>
        get() {
            val provinceList = mutableListOf<Province>()
            val cursor = db?.rawQuery("select * from $TABLE_ADDRESS_DICT where $ADDRESS_DICT_FIELD_PARENTID=?",
                    arrayOf(0.toString()))
            cursor?.run {
                while (moveToNext()) {
                    val province = Province(
                        getInt(getColumnIndex(ADDRESS_DICT_FIELD_ID)),
                        getString(getColumnIndex(ADDRESS_DICT_FIELD_CODE)),
                        getString(getColumnIndex(ADDRESS_DICT_FIELD_NAME))
                    )
                    provinceList.add(province)
                }
                cursor.close()
            }
            return provinceList
        }


    /* 增加 */
    /**
     * 增加一个地址
     * @param adress
     */
    fun insertAddress(address: ChangeRecordsBean?) {
        address?.run {
            db?.let {
                it.beginTransaction()//手动设置开启事务
                try {
                    val values = ContentValues()
                    values.put(ADDRESS_DICT_FIELD_ID, id)
                    values.put(ADDRESS_DICT_FIELD_PARENTID, parentId)
                    values.put(ADDRESS_DICT_FIELD_CODE, code)
                    values.put(ADDRESS_DICT_FIELD_NAME, name)
                    it.insert(TABLE_ADDRESS_DICT, null, values)
                    it.setTransactionSuccessful() //设置事务处理成功
                } catch (e: Exception) {

                } finally {
                    it.endTransaction() //事务终止
                }
            }
        }
    }

    /**
     * 增加地址集合
     * @param list
     */
    fun insertAddressList(list: MutableList<ChangeRecordsBean>?) {
        list?.run {
            db?.let {
                it.beginTransaction()//手动设置开启事务
                try {
                    for (address in this){
                        val values = ContentValues()
                        values.put(ADDRESS_DICT_FIELD_ID, address.id)
                        values.put(ADDRESS_DICT_FIELD_PARENTID, address.parentId)
                        values.put(ADDRESS_DICT_FIELD_CODE, address.code)
                        values.put(ADDRESS_DICT_FIELD_NAME, address.name)
                        it.insert(TABLE_ADDRESS_DICT, null, values)
                    }
                    it.setTransactionSuccessful() //设置事务处理成功
                } catch (e: Exception) {
                } finally {
                    it.endTransaction() //事务终止
                }
            }
        }
    }

    /* 更新 */
    /**
     * 更新地址
     */
    fun updateAddressInfo(address: ChangeRecordsBean?) {
        address?.run {
            db?.let {
                it.beginTransaction()//手动设置开启事务
                try {
                    val values = ContentValues()
                    values.put(ADDRESS_DICT_FIELD_ID, id)
                    values.put(ADDRESS_DICT_FIELD_PARENTID, parentId)
                    values.put(ADDRESS_DICT_FIELD_CODE, code)
                    values.put(ADDRESS_DICT_FIELD_NAME, name)
                    it.update(TABLE_ADDRESS_DICT, values, "${TableField.FIELD_ID}=?", arrayOf("$id"))
                    it.setTransactionSuccessful() //设置事务处理成功
                } catch (e: Exception) {

                } finally {
                    it.endTransaction() //事务终止
                }
            }
        }
    }

    /* 查找 */
    /**
     * 根据"provinceCode(省code)" -- 获取 -- 省份名字
     * @param provinceCode
     * @return
     */
    fun getProvince(provinceCode: String): String {
        val cursor = db?.rawQuery("select * from $TABLE_ADDRESS_DICT where $ADDRESS_DICT_FIELD_CODE=?",
                arrayOf(provinceCode))

        var name = ""
        cursor?.run {
            if (moveToFirst()) name = getString(getColumnIndex(ADDRESS_DICT_FIELD_NAME))

            close()
        }
        return name
    }

    /**
     * 获取 -- 省份 -- 根据"provinceCode(省code)"
     * @param provinceCode
     * @return
     */
    fun getProvinceBean(provinceCode: String): Province? {
        val cursor = db?.rawQuery("select * from $TABLE_ADDRESS_DICT where $ADDRESS_DICT_FIELD_CODE=?",
            arrayOf(provinceCode))

        var province : Province? = null
        cursor?.run {
            if (moveToFirst()) {
                province = Province(
                    getInt(getColumnIndex(ADDRESS_DICT_FIELD_ID)),
                    getString(getColumnIndex(ADDRESS_DICT_FIELD_CODE)),
                    getString(getColumnIndex(ADDRESS_DICT_FIELD_NAME))
                )
            }
            close()
        }
        return province
    }

    /**
     * 获取 -- 城市列表 -- 根据"provinceId(省id)"
     * @param provinceId
     * @return
     */
    fun getCityList(provinceId: Int): MutableList<City> {
        val cityList = mutableListOf<City>()
        val cursor = db?.rawQuery("select * from $TABLE_ADDRESS_DICT where $ADDRESS_DICT_FIELD_PARENTID=?",
                arrayOf(provinceId.toString()))
        cursor?.run {
            while (moveToNext()){
                val city = City(
                    getInt(getColumnIndex(ADDRESS_DICT_FIELD_ID)),
                    getString(getColumnIndex(ADDRESS_DICT_FIELD_CODE)),
                    getString(getColumnIndex(ADDRESS_DICT_FIELD_NAME))
                )
                cityList.add(city)
            }
            close()
        }
        return cityList
    }

    /**
     * 获取 -- 城市名字 -- 根据"cityCode(城市Code)"
     * @param cityCode
     * @return
     */
    fun getCity(cityCode: String): String {
        val cursor = db?.rawQuery("select * from $TABLE_ADDRESS_DICT where $ADDRESS_DICT_FIELD_CODE=?",
                arrayOf(cityCode))

        var name = ""
        cursor?.run {
            if (moveToFirst())name = getString(getColumnIndex(ADDRESS_DICT_FIELD_NAME))

            close()
        }
        return name
    }

    /**
     * 获取 -- 城市 -- 根据"cityCode(城市Code)"
     * @param cityCode
     * @return
     */
    fun getCityBean(cityCode: String): City? {
        val cursor = db?.rawQuery("select * from $TABLE_ADDRESS_DICT where $ADDRESS_DICT_FIELD_CODE=?",
            arrayOf(cityCode))

        var city : City? = null
        cursor?.run {
            if (moveToFirst()){
                city = City(
                    getInt(getColumnIndex(ADDRESS_DICT_FIELD_ID)),
                    getString(getColumnIndex(ADDRESS_DICT_FIELD_CODE)),
                    getString(getColumnIndex(ADDRESS_DICT_FIELD_NAME))
                )
            }

            close()
        }
        return city
    }


    /**
     * 获取 -- 区，乡镇列表 --  根据"cityId(城市id)"
     * @param cityId
     * @return
     */
    fun getCountyList(cityId: Int): MutableList<County> {
        val countyList = mutableListOf<County>()
        val cursor = db?.rawQuery(
            "select * from $TABLE_ADDRESS_DICT where $ADDRESS_DICT_FIELD_PARENTID=?",
            arrayOf(cityId.toString())
        )

        cursor?.run {
            while (moveToNext()){
                val county = County(
                    getInt(getColumnIndex(ADDRESS_DICT_FIELD_ID)),
                    getString(getColumnIndex(ADDRESS_DICT_FIELD_CODE)),
                    getString(getColumnIndex(ADDRESS_DICT_FIELD_NAME))
                )
                countyList.add(county)
            }
            close()
        }
        return countyList
    }



    /**
     * 获取 -- 区，乡镇名称 --  根据"countyCode(区，乡镇Code)"
     * @param countyCode
     * @return
     */
    fun getCounty(countyCode: String): String {
        val cursor = db?.rawQuery(
            "select * from $TABLE_ADDRESS_DICT where ADDRESS_DICT_FIELD_CODE =?",
            arrayOf(countyCode)
        )

        var name = ""
        cursor?.run {
            if (moveToFirst())name = getString(getColumnIndex(ADDRESS_DICT_FIELD_NAME))

            close()
        }
        return name
    }

    /**
     * 获取 -- 区，乡镇 -- 根据"countyCode(区，乡镇Code)"
     * @param cityCode
     * @return
     */
    fun getCountyBean(countyCode: String): County? {
        val cursor = db?.rawQuery(
            "select * from $TABLE_ADDRESS_DICT where $ADDRESS_DICT_FIELD_CODE=?",
            arrayOf(countyCode)
        )

        var county : County? = null
        cursor?.run {
            if (moveToFirst()){
                county = County(
                    getInt(getColumnIndex(ADDRESS_DICT_FIELD_ID)),
                    getString(getColumnIndex(ADDRESS_DICT_FIELD_CODE)),
                    getString(getColumnIndex(ADDRESS_DICT_FIELD_NAME))
                )
            }

            close()
        }
        return county
    }


    /**
     * 获取 -- 街道列表 --  根据"countyId(区，乡镇id)"
     * @param countyId
     * @return
     */
    fun getStreetList(countyId: Int): MutableList<Street> {
        val streetList = mutableListOf<Street>()
        val cursor = db?.rawQuery(
            "select * from $TABLE_ADDRESS_DICT where $ADDRESS_DICT_FIELD_PARENTID=?",
            arrayOf(countyId.toString())
        )

        cursor?.run {
            while (moveToNext()){
                val street = Street(
                    getInt(getColumnIndex(ADDRESS_DICT_FIELD_ID)),
                    getString(getColumnIndex(ADDRESS_DICT_FIELD_CODE)),
                    getString(getColumnIndex(ADDRESS_DICT_FIELD_NAME))
                )
                streetList.add(street)
            }
            close()
        }
        return streetList
    }

    /**
     * 获取 -- 街道名称 --  根据"streetCode(街道Code)"
     * @param countyCode
     * @return
     */
    fun getStreet(streetCode: String): String {
        val cursor = db?.rawQuery(
            "select * from $TABLE_ADDRESS_DICT where $ADDRESS_DICT_FIELD_CODE=?",
            arrayOf(streetCode)
        )

        var name = ""
        cursor?.run {
            if (moveToFirst())name = getString(getColumnIndex(ADDRESS_DICT_FIELD_NAME))

            close()
        }
        return name
    }


    /**
     * 获取 -- 街道 -- 根据"streetCode(街道Code)"
     * @param cityCode
     * @return
     */
    fun getStreetBean(streetCode: String): Street? {
        val cursor = db?.rawQuery(
            "select * from $TABLE_ADDRESS_DICT where $ADDRESS_DICT_FIELD_CODE=?",
            arrayOf(streetCode)
        )

        var street : Street? = null
        cursor?.run {
            if (moveToFirst()){
                street = Street(
                    getInt(getColumnIndex(ADDRESS_DICT_FIELD_ID)),
                    getString(getColumnIndex(ADDRESS_DICT_FIELD_CODE)),
                    getString(getColumnIndex(ADDRESS_DICT_FIELD_NAME))
                )
            }

            close()
        }
        return street
    }



    /**
     * 查找消息临时列表中是否存在这一条记录
     * @param bannerInfo banner数据
     * @return
     */
    fun isExist(bannerInfo: ChangeRecordsBean): Int {
        var count = 0
        val cursor = db?.rawQuery(
            "select count(*) from $TABLE_ADDRESS_DICT where $ADDRESS_DICT_FIELD_ID =?",
            arrayOf("${bannerInfo.id}")
        )

        cursor?.run {
            if (moveToFirst()) count = getInt(0)

            close()
        }
        return count
    }
}
