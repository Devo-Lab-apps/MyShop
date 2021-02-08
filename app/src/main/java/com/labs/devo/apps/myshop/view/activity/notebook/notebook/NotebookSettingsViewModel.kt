package com.labs.devo.apps.myshop.view.activity.notebook.notebook

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.business.notebook.abstraction.NotebookRepository
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.launch

class NotebookSettingsViewModel
@ViewModelInject constructor(val notebookRepository: NotebookRepository) :
    BaseViewModel<NotebookSettingsViewModel.NotebookSettingsEvent>() {


    fun updateNotebook(prevNotebook: Notebook, notebook: Notebook) = viewModelScope.launch {
        if (prevNotebook.notebookId != notebook.notebookId) {
            channel.send(NotebookSettingsEvent.ShowInvalidInputMessage("Notebook being updated is different"))
            return@launch
        }
        if (notebook.notebookName == prevNotebook.notebookName) {
            channel.send(NotebookSettingsEvent.ShowInvalidInputMessage("Notebook's name is not changed"))
            return@launch
        }
        val data = notebookRepository.updateNotebook(notebook)
        data.data?.let {
            channel.send(NotebookSettingsEvent.NotebookUpdated("Notebook is updated"))
        }
            ?: channel.send(NotebookSettingsEvent.ShowInvalidInputMessage(data.message?.getContentIfNotHandled()))
    }

    fun deleteNotebook(notebook: Notebook) = viewModelScope.launch {
        val data = notebookRepository.deleteNotebook(notebook)
        data.data?.let {
            channel.send(NotebookSettingsEvent.NotebookDeleted("Notebook is deleted"))
        }
            ?: channel.send(NotebookSettingsEvent.ShowInvalidInputMessage(data.message?.getContentIfNotHandled()))
    }

    sealed class NotebookSettingsEvent {

        data class NotebookUpdated(val msg: String) : NotebookSettingsEvent()

        data class NotebookDeleted(val msg: String) : NotebookSettingsEvent()

        data class ShowInvalidInputMessage(val msg: String?) : NotebookSettingsEvent()
    }

}