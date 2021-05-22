package com.labs.devo.apps.myshop.view.activity.notebook.notebook

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.NotebookRepository
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.NOTEBOOK
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.AddEditNotebookViewModel.AddEditNotebookEvents.NOTEBOOK_DELETED
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.AddEditNotebookViewModel.AddEditNotebookEvents.NOTEBOOK_DIFFERENT_ERR
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.AddEditNotebookViewModel.AddEditNotebookEvents.NOTEBOOK_INSERTED
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.AddEditNotebookViewModel.AddEditNotebookEvents.NOTEBOOK_NAME_NOT_CHANGED_ERR
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.AddEditNotebookViewModel.AddEditNotebookEvents.NOTEBOOK_UPDATED
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.launch

class AddEditNotebookViewModel @ViewModelInject
constructor(
    private val notebookRepository: NotebookRepository,
    @Assisted val state: SavedStateHandle
) :
    BaseViewModel<AddEditNotebookViewModel.AddEditNotebookEvent>() {

    val notebook = state.get<Notebook>(NOTEBOOK)

    val operation = state.get<String>(OPERATION)

    fun addNotebook(notebook: Notebook) = viewModelScope.launch {
        val res = notebookRepository.createNotebook(notebook)
        res.data?.let {
            channel.send(AddEditNotebookEvent.NotebookInserted(NOTEBOOK_INSERTED))
        }
            ?: channel.send(AddEditNotebookEvent.ShowInvalidInputMessage(res.message?.getContentIfNotHandled()))
    }

    fun updateNotebook(prevNotebook: Notebook, notebook: Notebook) = viewModelScope.launch {
        if (prevNotebook.notebookId != notebook.notebookId) {
            channel.send(AddEditNotebookEvent.ShowInvalidInputMessage(NOTEBOOK_DIFFERENT_ERR))
            return@launch
        }
        if (notebook.notebookName == prevNotebook.notebookName) {
            channel.send(AddEditNotebookEvent.ShowInvalidInputMessage(NOTEBOOK_NAME_NOT_CHANGED_ERR))
            return@launch
        }
        val data = notebookRepository.updateNotebook(notebook)
        data.data?.let {
            channel.send(AddEditNotebookEvent.NotebookUpdated(NOTEBOOK_UPDATED))
        }
            ?: channel.send(
                AddEditNotebookEvent.ShowInvalidInputMessage(
                    data.message?.getContentIfNotHandled()
                )
            )
    }

    fun deleteNotebook(notebook: Notebook) = viewModelScope.launch {
        val data = notebookRepository.deleteNotebook(notebook)
        data.data?.let {
            channel.send(AddEditNotebookEvent.NotebookDeleted(NOTEBOOK_DELETED))
        }
            ?: channel.send(
                AddEditNotebookEvent.ShowInvalidInputMessage(
                    data.message?.getContentIfNotHandled()
                )
            )
    }

    sealed class AddEditNotebookEvent {

        data class ShowInvalidInputMessage(val msg: String?) : AddEditNotebookEvent()

        data class NotebookInserted(val msg: String) : AddEditNotebookEvent()

        data class NotebookUpdated(val msg: String) :
            AddEditNotebookViewModel.AddEditNotebookEvent()

        data class NotebookDeleted(val msg: String) :
            AddEditNotebookViewModel.AddEditNotebookEvent()


    }

    object AddEditNotebookEvents {
        const val NOTEBOOK_INSERTED = "Notebook is added."
        const val NOTEBOOK_UPDATED = "Notebook is updated."
        const val NOTEBOOK_DIFFERENT_ERR = "Notebook being updated is different"
        const val NOTEBOOK_NAME_NOT_CHANGED_ERR = "Notebook's name is not changed"
        const val NOTEBOOK_DELETED = "Notebook is deleted"
    }
}