package com.labs.devo.apps.myshop.data.db.local.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.labs.devo.apps.myshop.data.models.notebook.Entry


@Dao
interface EntryDao {

    fun getEntries(
        pageId: String,
        searchQuery: String,
        orderBy: String,
        isRepeating: Boolean
    ): PagingSource<Int, Entry> = when (orderBy) {
        Entry::entryTitle.name -> getEntriesOrderByTitle(pageId, searchQuery, isRepeating)
        else -> getEntriesOrderByModifiedAt(pageId, searchQuery, isRepeating)
    }


    @Query("SELECT * FROM Entry WHERE pageId = :pageId and (entryTitle LIKE :searchQuery or entryDescription LIKE :searchQuery) and isRepeating = :isRepeating ORDER BY entryTitle ASC")
    fun getEntriesOrderByTitle(
        pageId: String,
        searchQuery: String,
        isRepeating: Boolean
    ): PagingSource<Int, Entry>

    @Query("SELECT * FROM Entry WHERE pageId = :pageId and (entryTitle LIKE :searchQuery or entryDescription LIKE :searchQuery) and isRepeating = :isRepeating ORDER BY modifiedAt DESC")
    fun getEntriesOrderByModifiedAt(
        pageId: String,
        searchQuery: String,
        isRepeating: Boolean
    ): PagingSource<Int, Entry>

    fun getEntriesLikeEntryId(
        entryId: String,
        dateRange: Pair<Long, Long>,
        orderBy: String,
        isRepeating: Boolean
    ): PagingSource<Int, Entry> = when (orderBy) {
        Entry::entryTitle.name -> getEntriesOrderByTitleLikeEntryId(
            entryId,
            dateRange.first,
            dateRange.second,
            isRepeating
        )
        else -> getEntriesOrderByModifiedAtLikeEntryId(
            entryId,
            dateRange.first,
            dateRange.second,
            isRepeating
        )
    }

    @Query("SELECT * FROM entry where pageId = :pageId and entryId LIKE :searchQuery ORDER BY fetchedAt LIMIT 1")
    fun getLastFetchedEntry(
        pageId: String,
        searchQuery: String
    ): Entry?

    @Query("SELECT * FROM Entry WHERE entryId LIKE :pageId and createdAt >= :start and createdAt <= :end and isRepeating = :isRepeating ORDER BY entryTitle ASC")
    fun getEntriesOrderByTitleLikeEntryId(
        pageId: String,
        start: Long,
        end: Long,
        isRepeating: Boolean
    ): PagingSource<Int, Entry>

    @Query("SELECT * FROM Entry WHERE entryId LIKE :pageId and createdAt >= :start and createdAt <= :end and isRepeating = :isRepeating ORDER BY modifiedAt DESC")
    fun getEntriesOrderByModifiedAtLikeEntryId(
        pageId: String,
        start: Long,
        end: Long,
        isRepeating: Boolean
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

    @Query("DELETE FROM entry where entryId LIKE :entryId")
    suspend fun deleteEntriesLikeEntryId(entryId: String)

    @Query("DELETE FROM Entry where pageId = :pageId")
    suspend fun deleteEntries(pageId: String)

    @Query("DELETE FROM Entry")
    suspend fun deleteAll()

}