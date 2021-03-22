package com.labs.devo.apps.myshop.view.activity.notebook.notebook

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.NotebookRepository
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.util.printLogD
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
                    ex.message ?: "Can't fetch the notebooks. Please retry"
                )
            )
        }
    }

    private suspend fun handleGetNotebooks(dataState: DataState<List<Notebook>>) {
        dataState.data?.let { event ->
            val notebooks = event.getContentIfNotHandled()
            if (notebooks.isNullOrEmpty()) {
                dataState.message = Event.messageEvent("No notebooks in this account.")
                printLogD("No account in this account.")
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
                    ex.message ?: "Can't fetch the notebooks. Please retry"
                )
            )
        }
    }


    sealed class NotebookEvent {

        data class ShowInvalidInputMessage(val msg: String?) : NotebookEvent()

        data class GetNotebooks(
            val notebooks: List<Notebook>,
            val dataState: DataState<List<Notebook>>
        ) : NotebookEvent()

        object NotebookInserted : NotebookEvent()

    }
}