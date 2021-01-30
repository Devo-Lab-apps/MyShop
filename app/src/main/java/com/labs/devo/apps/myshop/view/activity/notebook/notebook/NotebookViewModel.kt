package com.labs.devo.apps.myshop.view.activity.notebook.notebook

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.business.notebook.abstraction.NotebookRepository
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NotebookViewModel @ViewModelInject constructor(val notebookRepository: NotebookRepository) :
    BaseViewModel<NotebookViewModel.NotebookEvent>() {


    fun getNotebooks() = viewModelScope.launch {
        try {
            notebookRepository.getNotebooks().collect { notebooks ->
                channel.send(NotebookEvent.GetNotebooks(notebooks))
            }
        } catch (ex: Exception) {
            channel.send(
                NotebookEvent.ShowInvalidInputMessage(
                    ex.message ?: "Can't fetch the notebook. Please retry"
                )
            )
        }
    }

    fun insertNotebook(notebook: Notebook) = viewModelScope.launch {
        val data = notebookRepository.insertNotebook(notebook)
        data.data?.let {
            channel.send(NotebookEvent.NotebookInserted)
        }
            ?: channel.send(NotebookEvent.ShowInvalidInputMessage(data.message?.getContentIfNotHandled()))
    }

    sealed class NotebookEvent {

        data class ShowInvalidInputMessage(val msg: String?) : NotebookEvent()

        data class GetNotebooks(val notebooks: List<Notebook>) : NotebookEvent()

        object NotebookInserted : NotebookEvent()

        object NotebookUpdated : NotebookEvent()

        object NotebookDeleted : NotebookEvent()
    }
}