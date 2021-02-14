package com.labs.devo.apps.myshop.data.db.local.database.dao

import androidx.room.*
import com.labs.devo.apps.myshop.data.db.local.models.notebook.LocalEntityEntry


@Dao
interface EntryDao {

    @Query("SELECT * FROM Entry WHERE pageId = :pageId")
    fun getEntries(pageId: String): List<LocalEntityEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: LocalEntityEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<LocalEntityEntry>)

    @Update
    suspend fun updateEntry(entry: LocalEntityEntry)

    @Update
    suspend fun updateEntries(entries: List<LocalEntityEntry>)

    @Delete
    suspend fun deleteEntry(entry: LocalEntityEntry)

    @Delete
    suspend fun deleteEntries(entries: List<LocalEntityEntry>)

    @Query("DELETE FROM Entry where pageId = :pageId")
    suspend fun deleteEntries(pageId: String)

}