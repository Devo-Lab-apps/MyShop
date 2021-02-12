package com.labs.devo.apps.myshop.data.db.local.database.dao

import androidx.room.*
import com.labs.devo.apps.myshop.data.db.local.models.notebook.LocalEntityNotebook

@Dao
interface NotebookDao {

    @Query("SELECT * FROM Notebook")
    fun getNotebooks(): List<LocalEntityNotebook>

    @Query("SELECT * FROM Notebook WHERE notebookId = :notebookId")
    fun getNotebook(notebookId: String): LocalEntityNotebook

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotebook(notebook: LocalEntityNotebook)

    @Insert
    suspend fun insertNotebooks(notebooks: List<LocalEntityNotebook>)

    @Update
    suspend fun updateNotebook(notebook: LocalEntityNotebook)

    @Update
    suspend fun updateNotebooks(notebooks: List<LocalEntityNotebook>)

    @Delete
    suspend fun deleteNotebook(notebook: LocalEntityNotebook)

    @Delete
    suspend fun deleteNotebooks(notebooks: List<LocalEntityNotebook>)

    @Query("DELETE FROM NOTEBOOK")
    suspend fun deleteNotebooks()

}