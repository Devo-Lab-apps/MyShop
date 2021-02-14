package com.labs.devo.apps.myshop.data.db.local.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.labs.devo.apps.myshop.data.db.local.database.dao.EntryDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.NotebookDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.PageDao
import com.labs.devo.apps.myshop.data.db.local.database.util.Converters
import com.labs.devo.apps.myshop.data.db.local.models.notebook.LocalEntityEntry
import com.labs.devo.apps.myshop.data.db.local.models.notebook.LocalEntityNotebook
import com.labs.devo.apps.myshop.data.db.local.models.notebook.LocalEntityPage


@Database(
    entities = [LocalEntityPage::class, LocalEntityNotebook::class, LocalEntityEntry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NotebookDatabase : RoomDatabase() {

    abstract fun pageDao(): PageDao

    abstract fun notebookDao(): NotebookDao

    abstract fun entryDao(): EntryDao

}