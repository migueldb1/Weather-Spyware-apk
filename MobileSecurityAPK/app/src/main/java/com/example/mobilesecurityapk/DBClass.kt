package com.example.mobilesecurityapk

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBClass(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "WeatherDatabase"
        const val TABLE_CONTACTS = "user"
        const val KEY_NAME = "name"
        const val KEY_UNAME = "username"
        const val KEY_PSWD = "pswd"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val newtb = "CREATE TABLE $TABLE_CONTACTS ($KEY_NAME TEXT, $KEY_UNAME TEXT, $KEY_PSWD TEXT)"
        db?.execSQL(newtb)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }
}
