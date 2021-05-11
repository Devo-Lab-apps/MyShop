package com.labs.devo.apps.myshop.data.db.local.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.labs.devo.apps.myshop.data.db.local.database.dao.item.ItemDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.item.ItemDetailDao
import com.labs.devo.apps.myshop.data.db.local.database.util.NotebookDatabaseConverter
import com.labs.devo.apps.myshop.data.models.item.Item
import com.labs.devo.apps.myshop.data.models.item.ItemDetail

@Database(
    entities = [Item::class, ItemDetail::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(NotebookDatabaseConverter::class)
abstract class ItemDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    abstract fun itemDetailDao(): ItemDetailDao

}