package com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook

import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import kotlinx.coroutines.flow.Flow

interface NotebookService {

    suspend fun getNotebooks(): Flow<List<Notebook>>

}