package com.labs.devo.apps.myshop.data.db.local.database.dao

import androidx.room.*
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry

@Dao
interface RecurringEntryDao {

    fun getRecurringEntries(
        pageId: String,
        searchQuery: String,
        orderBy: String
    ): List<RecurringEntry> = when (orderBy) {
        Entry::entryTitle.name -> getRecurringEntriesOrderByTitle(pageId, searchQuery)
        else -> getRecurringEntriesOrderByModifiedAt(pageId, searchQuery)
    }


    @Query("SELECT * FROM RecurringEntry WHERE pageId = :pageId and (name LIKE :searchQuery or description LIKE :searchQuery) ORDER BY name ASC")
    fun getRecurringEntriesOrderByTitle(
        pageId: String,
        searchQuery: String
    ): List<RecurringEntry>

    @Query("SELECT * FROM RecurringEntry WHERE pageId = :pageId and (name LIKE :searchQuery or description LIKE :searchQuery) ORDER BY modifiedAt ASC")
    fun getRecurringEntriesOrderByModifiedAt(
        pageId: String,
        searchQuery: String,
    ): List<RecurringEntry>

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
}