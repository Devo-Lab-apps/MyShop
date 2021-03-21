package com.labs.devo.apps.myshop.data.db.local.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.labs.devo.apps.myshop.data.models.notebook.Entry


@Dao
interface EntryDao {

    fun getEntries(
        pageId: String,
        searchQuery: String,
        orderBy: String
    ): PagingSource<Int, Entry> = when (orderBy) {
        Entry::entryTitle.name -> getPagesOrderByTitle(pageId, searchQuery)
        else -> getPagesOrderByModifiedAt(pageId, searchQuery)
    }


    @Query("SELECT * FROM Entry WHERE pageId = :pageId and (entryTitle LIKE :searchQuery or entryDescription LIKE :searchQuery) ORDER BY entryTitle ASC")
    fun getPagesOrderByTitle(
        pageId: String,
        searchQuery: String
    ): PagingSource<Int, Entry>

    @Query("SELECT * FROM Entry WHERE pageId = :pageId and (entryTitle LIKE :searchQuery or entryDescription LIKE :searchQuery) ORDER BY modifiedAt ASC")
    fun getPagesOrderByModifiedAt(
        pageId: String,
        searchQuery: String,
    ): PagingSource<Int, Entry>


    @Query("SELECT * FROM Entry WHERE entryId = :entryId")
    fun getEntry(
        entryId: String,

        ): Entry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: Entry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<Entry>)

    @Update
    suspend fun updateEntry(entry: Entry)

    @Update
    suspend fun updateEntries(entries: List<Entry>)

    @Delete
    suspend fun deleteEntry(entry: Entry)

    @Delete
    suspend fun deleteEntries(entries: List<Entry>)

    @Query("DELETE FROM Entry where pageId = :pageId")
    suspend fun deleteEntries(pageId: String)

    @Query("DELETE FROM Entry")
    suspend fun deleteAll()

}