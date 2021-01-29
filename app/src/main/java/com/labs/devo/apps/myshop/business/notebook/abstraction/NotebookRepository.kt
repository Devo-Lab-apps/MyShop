package com.labs.devo.apps.myshop.business.notebook.abstraction

import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import kotlinx.coroutines.flow.Flow

interface NotebookRepository {

    suspend fun getNotebooks(): Flow<List<Notebook>>


}