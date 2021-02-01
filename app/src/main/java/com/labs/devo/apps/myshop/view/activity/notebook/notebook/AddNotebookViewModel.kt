package com.labs.devo.apps.myshop.view.activity.notebook.notebook

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.business.notebook.abstraction.NotebookRepository
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.launch

class AddNotebookViewModel @ViewModelInject
constructor(val notebookRepository: NotebookRepository) :
    BaseViewModel<AddNotebookViewModel.AddNotebookEvent>() {


    fun addNotebook(notebook: Notebook) = viewModelScope.launch {
        val res = notebookRepository.insertNotebook(notebook)
        res.data?.let {
            channel.send(AddNotebookEvent.NotebookInserted("Notebook inserted"))
        }
            ?: channel.send(AddNotebookEvent.ShowInvalidInputMessage(res.message?.getContentIfNotHandled()))
    }

    sealed class AddNotebookEvent {

        data class ShowInvalidInputMessage(val msg: String?) : AddNotebookEvent()

        data class NotebookInserted(val msg: String) : AddNotebookEvent()

    }
}