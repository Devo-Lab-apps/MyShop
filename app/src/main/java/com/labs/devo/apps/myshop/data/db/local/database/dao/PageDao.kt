package com.labs.devo.apps.myshop.data.db.local.database.dao

import androidx.room.*
import com.labs.devo.apps.myshop.data.db.local.models.notebook.LocalEntityPage
import kotlinx.coroutines.flow.Flow


@Dao
interface PageDao {

    @Query("SELECT * FROM PAGE WHERE creatorNotebookId = :notebookId")
    fun getPages(notebookId: String): List<LocalEntityPage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: LocalEntityPage)

    @Insert
    suspend fun insertPages(pages: List<LocalEntityPage>)

    @Update
    suspend fun updatePage(page: LocalEntityPage)

    @Update
    suspend fun updatePages(pages: List<LocalEntityPage>)

    @Delete
    suspend fun deletePage(page: LocalEntityPage)

    @Delete
    suspend fun deletePages(pages: List<LocalEntityPage>)

}