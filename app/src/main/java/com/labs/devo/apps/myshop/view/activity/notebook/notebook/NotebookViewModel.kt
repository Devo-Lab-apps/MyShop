package com.labs.devo.apps.myshop.view.activity.notebook.notebook

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.NotebookRepository
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.NotebookViewModel.NotebookOperationConstants.CANT_FETCH_NOTEBOOKS_ERR
import com.labs.devo.apps.myshop.view.activity.notebook.notebook.NotebookViewModel.NotebookOperationConstants.NO_NOTEBOOKS_MSG
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.Event
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NotebookViewModel @ViewModelInject constructor(private val notebookRepository: NotebookRepository) :
    BaseViewModel<NotebookViewModel.NotebookEvent>() {


    fun getNotebooks() = viewModelScope.launch {
        try {
            notebookRepository.getNotebooks().collect { dataState ->
                handleGetNotebooks(dataState)
            }
        } catch (ex: Exception) {
            channel.send(
                NotebookEvent.ShowInvalidInputMessage(
                    ex.message ?: CANT_FETCH_NOTEBOOKS_ERR
                )
            )
        }
    }

    private suspend fun handleGetNotebooks(dataState: DataState<List<Notebook>>) {
        dataState.data?.let { event ->
            val notebooks = event.getContentIfNotHandled()
            if (notebooks.isNullOrEmpty()) {
                dataState.message = Event.messageEvent(NO_NOTEBOOKS_MSG)
            }
            channel.send(
                NotebookEvent.GetNotebooks(
                    notebooks ?: listOf(),
                    dataState
                )
            )
        } ?: channel.send(
            NotebookEvent.GetNotebooks(
                listOf(),
                dataState
            )
        )
    }

    fun syncNotebooks() = viewModelScope.launch {
        try {
            val dataState = notebookRepository.syncNotebooks()
            handleGetNotebooks(dataState)
        } catch (ex: Exception) {
            channel.send(
                NotebookEvent.ShowInvalidInputMessage(
                    ex.message ?: CANT_FETCH_NOTEBOOKS_ERR
                )
            )
        }
    }

    fun addNotebook() = viewModelScope.launch {
        channel.send(NotebookEvent.AddNotebookEvent)
    }

    fun editNotebook(notebook: Notebook) = viewModelScope.launch {
        channel.send(NotebookEvent.EditNotebookEvent(notebook))
    }


    sealed class NotebookEvent {

        data class ShowInvalidInputMessage(val msg: String?) : NotebookEvent()

        data class GetNotebooks(
            val notebooks: List<Notebook>,
            val dataState: DataState<List<Notebook>>
        ) : NotebookEvent()

        object AddNotebookEvent : NotebookEvent()
        data class EditNotebookEvent(val notebook: Notebook) : NotebookEvent()

    }

    object NotebookOperationConstants {
        const val NO_NOTEBOOKS_MSG = "No notebooks in this account."
        const val CANT_FETCH_NOTEBOOKS_ERR = "Can't fetch the notebooks. Please retry"
    }
}