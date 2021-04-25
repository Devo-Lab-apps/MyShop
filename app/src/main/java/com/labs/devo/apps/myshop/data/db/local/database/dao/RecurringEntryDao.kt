package com.labs.devo.apps.myshop.data.db.local.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry

@Dao
interface RecurringEntryDao {

    @Query("SELECT * FROM RecurringEntry WHERE pageId = :pageId ORDER BY name ASC")
    fun getRecurringEntries(
        pageId: String = ""
    ): PagingSource<Int, RecurringEntry>

    @Query("SELECT * FROM RecurringEntry WHERE recurringEntryId = :recurringEntryId")
    fun getRecurringEntry(
        recurringEntryId: String,
        ): RecurringEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringEntry(entry: RecurringEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringEntries(entries: List<RecurringEntry>)

    @Update
    suspend fun updateRecurringEntry(entry: RecurringEntry)

    @Update
    suspend fun updateRecurringEntries(entries: List<RecurringEntry>)

    @Delete
    suspend fun deleteRecurringEntry(entry: RecurringEntry)

    @Delete
    suspend fun deleteRecurringEntries(entries: List<RecurringEntry>)

    @Query("DELETE FROM RecurringEntry where pageId = :pageId")
    suspend fun deleteRecurringEntries(pageId: String)

    @Query("DELETE FROM RecurringEntry")
    suspend fun deleteAll()

    @Query("SELECT * FROM RecurringEntry WHERE pageId = :pageId ORDER by fetchedAt DESC limit 1")
    suspend fun getLastFetchedRecurringEntry(pageId: String): RecurringEntry?

    @Query("SELECT * FROM recurringentry where pageId = :pageId and recurringEntryId LIKE :searchQuery ORDER BY fetchedAt LIMIT 1")
    suspend fun getLastFetchedEntry(pageId: String?, searchQuery: String): RecurringEntry?
}