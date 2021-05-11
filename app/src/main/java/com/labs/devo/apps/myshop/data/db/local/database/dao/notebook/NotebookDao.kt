package com.labs.devo.apps.myshop.data.db.local.database.dao.notebook

import androidx.room.*
import com.labs.devo.apps.myshop.data.models.notebook.Notebook

@Dao
interface NotebookDao {

    @Query("SELECT * FROM Notebook")
    fun getNotebooks(): List<Notebook>

    @Query("SELECT * FROM Notebook WHERE notebookId = :notebookId")
    fun getNotebook(notebookId: String): Notebook

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotebook(notebook: Notebook)

    @Insert
    suspend fun insertNotebooks(notebooks: List<Notebook>)

    @Update
    suspend fun updateNotebook(notebook: Notebook)

    @Update
    suspend fun updateNotebooks(notebooks: List<Notebook>)

    @Delete
    suspend fun deleteNotebook(notebook: Notebook)

    @Delete
    suspend fun deleteNotebooks(notebooks: List<Notebook>)

    @Query("DELETE FROM NOTEBOOK")
    suspend fun deleteNotebooks()

    @Query("SELECT * FROM Notebook ORDER BY fetchedAt DESC limit 1")
    suspend fun getLastFetchedNotebook(): Notebook?

}