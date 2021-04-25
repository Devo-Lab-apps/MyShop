package com.labs.devo.apps.myshop.data.db.local.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.labs.devo.apps.myshop.data.db.local.database.RemoteKey
import com.labs.devo.apps.myshop.data.db.local.database.dao.*
import com.labs.devo.apps.myshop.data.db.local.database.util.NotebookDatabaseConverter
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry


@Database(
    entities = [Page::class, Notebook::class, Entry::class, RemoteKey::class, RecurringEntry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(NotebookDatabaseConverter::class)
abstract class NotebookDatabase : RoomDatabase() {

    abstract fun pageDao(): PageDao

    abstract fun notebookDao(): NotebookDao

    abstract fun entryDao(): EntryDao

    abstract fun remoteKeyDao(): RemoteKeyDao

    abstract fun recurringEntryDao(): RecurringEntryDao

}