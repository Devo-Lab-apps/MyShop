package com.labs.devo.apps.myshop.data.db.local.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.labs.devo.apps.myshop.data.models.notebook.Page


@Dao
interface PageDao {

    fun getPages(
        notebookId: String,
        searchQuery: String,
        orderBy: String
    ): PagingSource<Int, Page> = when (orderBy) {
        Page::pageName.name -> getPagesOrderByName(notebookId, searchQuery)
        else -> getPagesOrderByModifiedAt(notebookId, searchQuery)
    }

    @Query("SELECT * FROM Page WHERE creatorNotebookId = :notebookId and pageName LIKE :searchQuery ORDER BY pageName ASC")
    fun getPagesOrderByName(
        notebookId: String,
        searchQuery: String
    ): PagingSource<Int, Page>

    @Query("SELECT * FROM Page WHERE creatorNotebookId = :notebookId and pageName LIKE :searchQuery ORDER BY modifiedAt ASC")
    fun getPagesOrderByModifiedAt(
        notebookId: String,
        searchQuery: String,
    ): PagingSource<Int, Page>

    @Query("SELECT * FROM Page WHERE pageId = :pageId")
    fun getPage(pageId: String): Page?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: Page)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPages(pages: List<Page>)

    @Update
    suspend fun updatePage(page: Page)

    @Update
    suspend fun updatePages(pages: List<Page>)

    @Delete
    suspend fun deletePage(page: Page)

    @Delete
    suspend fun deletePages(pages: List<Page>)

    @Query("DELETE FROM Page where creatorNotebookId = :notebookId")
    suspend fun deletePages(notebookId: String)

}