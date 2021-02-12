package com.labs.devo.apps.myshop.view.activity.notebook.notebook

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.business.notebook.abstraction.NotebookRepository
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NotebookViewModel @ViewModelInject constructor(val notebookRepository: NotebookRepository) :
    BaseViewModel<NotebookViewModel.NotebookEvent>() {


    fun getNotebooks() = viewModelScope.launch {
        try {
            notebookRepository.getNotebooks().collect { dataState ->
                dataState.data?.let {
                    channel.send(
                        NotebookEvent.GetNotebooks(
                            it.getContentIfNotHandled() ?: listOf(),
                            dataState
                        )
                    )
                }
                    ?: channel.send(NotebookEvent.ShowInvalidInputMessage(dataState.message?.getContentIfNotHandled()))

            }
        } catch (ex: Exception) {
            channel.send(
                NotebookEvent.ShowInvalidInputMessage(
                    ex.message ?: "Can't fetch the notebooks. Please retry"
                )
            )
        }
    }

    fun syncNotebooks() = viewModelScope.launch {
        try {
            val dataState = notebookRepository.syncNotebooks()
            dataState.data?.let {
                channel.send(
                    NotebookEvent.GetNotebooks(
                        it.getContentIfNotHandled() ?: listOf(),
                        dataState
                    )
                )
            }
                ?: channel.send(NotebookEvent.ShowInvalidInputMessage(dataState.message?.getContentIfNotHandled()))
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