package com.shen.library.db

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.util.Log

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.HashMap

/**
 * "Assets"资源的"db"管理
 */
class AssetsDatabaseManager private constructor(private val mContext: Context) {
    private val mDatabaseMap = HashMap<String, SQLiteDatabase>()
    private val mDatabaseFilePath: String = String.format(mDatabasePath, mContext.applicationInfo.packageName)

    companion object {
        private val tag = "AssetsDatabase"                      // for LogCat
        private val mDatabasePath = "/data/data/%s/database"    // %s is packageName

        @SuppressLint("StaticFieldLeak")
        @Volatile
        var instance: AssetsDatabaseManager? = null

        fun getInstance(context : Context): AssetsDatabaseManager {
            if (instance == null) {
                synchronized(AssetsDatabaseManager::class) {
                    if (instance == null) {
                        instance = AssetsDatabaseManager(context)
                    }
                }
            }
            return instance!!
        }

        fun closeAllDatabase() {
            instance?.let {
                for ((key, value) in it.mDatabaseMap) {
                    value.close()
                }
                it.mDatabaseMap.clear()
            }
        }
    }

    /**
     * 获取数据库的 -- 绝对路径
     */
    private fun getDatabaseFile(dbFile: String): String {
        return "$mDatabaseFilePath/$dbFile"
    }

    /**
     * 获取数据库 -- 如果这个数据库已打开了，则获取他的一个副本
     * @param dbFile        数据库名
     * @return
     */
    fun getDatabase(dbFile: String): SQLiteDatabase? {
        if (mDatabaseMap[dbFile] != null) {
            Log.i(tag, String.format("Return a database copy of %s", dbFile))
            return mDatabaseMap[dbFile]
        }

        Log.i(tag, String.format("Create database %s", dbFile))
        val sPath = mDatabaseFilePath
        val sFile = getDatabaseFile(dbFile)
        var file = File(sFile)
        val dbSP = mContext.getSharedPreferences(AssetsDatabaseManager::class.java.toString(), 0)
        val flag = dbSP.getBoolean(dbFile, false) // 获取数据库文件标志，如果为true，则表示此数据库文件已复制且有效
        if (!flag || !file.exists()) {
            file = File(sPath)
            if (!file.exists() && !file.mkdirs()) {
                Log.i(tag, "Create \"$sPath\" fail!")
                return null
            }
            if (!copyAssetsToFilesystem(dbFile, sFile)) {
                Log.i(tag, String.format("Copy %s to %s fail!", dbFile, sFile))
                return null
            }
            dbSP.edit().putBoolean(dbFile, true).apply()
        }

        val db = SQLiteDatabase.openDatabase(sFile, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS)
        if (db != null) {
            mDatabaseMap[dbFile] = db
        }
        return db
    }


    /**
     * 将 Assets 中的 文件 拷贝到对应的路径
     */
    private fun copyAssetsToFilesystem(assetsSrc: String, targetFilePath: String): Boolean {
        Log.i(tag, "Copy $assetsSrc to $targetFilePath")
        var iStream: InputStream? = null
        var oStream: OutputStream? = null
        try {
            val assetManager = mContext!!.assets
            iStream = assetManager.open(assetsSrc)
            oStream = FileOutputStream(targetFilePath)
            val buffer = ByteArray(1024)
            var length: Int
            while (true) {
                length = iStream.read(buffer)
                if (length <= 0)
                    break
                oStream.write(buffer, 0, length)
            }

            iStream.close()
            oStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                iStream?.close()
                oStream?.close()
            } catch (ee: Exception) {
                ee.printStackTrace()
            }
            return false
        }
        return true
    }

    /**
     * 关闭 -- 数据库（指定的）
     */
    fun closeDatabase(dbFile: String): Boolean {
        mDatabaseMap[dbFile]?.let {
            it.close()
            mDatabaseMap.remove(dbFile)
            return true
        }
        return false
    }


}