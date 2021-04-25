package com.labs.devo.apps.myshop.data.db.local.database.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import javax.inject.Singleton

@Singleton
object AppDatabaseConverter {

    val gson = Gson()

    @TypeConverter
    @JvmStatic
    fun fromString(value: String): List<Long> {
        val listType: Type = object : TypeToken<ArrayList<Long>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    @JvmStatic
    fun fromList(list: List<Long>): String {
        return gson.toJson(list)
    }
}