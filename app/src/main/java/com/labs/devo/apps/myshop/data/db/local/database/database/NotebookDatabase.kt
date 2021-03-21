package com.labs.devo.apps.myshop.data.db.local.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.labs.devo.apps.myshop.data.db.local.database.RemoteKey
import com.labs.devo.apps.myshop.data.db.local.database.dao.EntryDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.NotebookDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.PageDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.RemoteKeyDao
import com.labs.devo.apps.myshop.data.db.local.database.util.Converters
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.data.models.notebook.Page


@Database(
    entities = [Page::class, Notebook::class, Entry::class, RemoteKey::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NotebookDatabase : RoomDatabase() {

    abstract fun pageDao(): PageDao

    abstract fun notebookDao(): NotebookDao

    abstract fun entryDao(): EntryDao

    abstract fun remoteKeyDao(): RemoteKeyDao

}