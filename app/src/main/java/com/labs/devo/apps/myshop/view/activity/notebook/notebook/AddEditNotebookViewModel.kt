package com.labs.devo.apps.myshop.view.activity.notebook.notebook

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.business.notebook.abstraction.NotebookRepository
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.launch

class AddEditNotebookViewModel @ViewModelInject
constructor(val notebookRepository: NotebookRepository) :
    BaseViewModel<AddEditNotebookViewModel.AddEditNotebookEvent>() {


    fun addNotebook(notebook: Notebook) = viewModelScope.launch {
        val res = notebookRepository.insertNotebook(notebook)
        res.data?.let {
            channel.send(AddEditNotebookEvent.NotebookInserted("Notebook inserted"))
        }
            ?: channel.send(AddEditNotebookEvent.ShowInvalidInputMessage(res.message?.getContentIfNotHandled()))
    }

    fun updateNotebook(prevNotebook: Notebook, notebook: Notebook) = viewModelScope.launch {
        if (prevNotebook.notebookId != notebook.notebookId) {
            channel.send(AddEditNotebookEvent.ShowInvalidInputMessage("Notebook being updated is different"))
            return@launch
        }
        if (notebook.notebookName == prevNotebook.notebookName) {
            channel.send(AddEditNotebookEvent.ShowInvalidInputMessage("Notebook's name is not changed"))
            return@launch
        }
        val data = notebookRepository.updateNotebook(notebook)
        data.data?.let {
            channel.send(AddEditNotebookEvent.NotebookUpdated("Notebook is updated"))
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
            channel.send(AddEditNotebookEvent.NotebookDeleted("Notebook is deleted"))
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
}