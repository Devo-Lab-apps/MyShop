package com.labs.devo.apps.myshop.data.db.local.database.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


object Converters {
    @TypeConverter
    @JvmStatic
    fun fromString(value: String): List<String> {
        val listType: Type = object : TypeToken<ArrayList<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    @JvmStatic
    fun fromList(list: List<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}