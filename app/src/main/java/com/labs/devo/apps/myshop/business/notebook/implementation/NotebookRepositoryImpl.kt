package com.labs.devo.apps.myshop.business.notebook.implementation

import com.labs.devo.apps.myshop.business.notebook.abstraction.NotebookRepository
import com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook.NotebookService
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotebookRepositoryImpl @Inject constructor(val notebookService: NotebookService): NotebookRepository {


    override suspend fun getNotebooks(): Flow<List<Notebook>> = notebookService.getNotebooks()
}