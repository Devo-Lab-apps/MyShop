package com.labs.devo.apps.myshop.data.db.local.database.dao

import androidx.room.*
import com.labs.devo.apps.myshop.data.db.local.models.notebook.LocalEntityPage


@Dao
interface PageDao {

    @Query("SELECT * FROM PAGE WHERE creatorNotebookId = :notebookId and pageName LIKE '%' || :searchQuery || '%' ORDER BY createdAt DESC")
    fun getPages(notebookId: String, searchQuery: String): List<LocalEntityPage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: LocalEntityPage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPages(pages: List<LocalEntityPage>)

    @Update
    suspend fun updatePage(page: LocalEntityPage)

    @Update
    suspend fun updatePages(pages: List<LocalEntityPage>)

    @Delete
    suspend fun deletePage(page: LocalEntityPage)

    @Delete
    suspend fun deletePages(pages: List<LocalEntityPage>)

    @Query("DELETE FROM Page where creatorNotebookId = :notebookId")
    suspend fun deletePages(notebookId: String)

}