package com.labs.devo.apps.myshop.view.activity.notebook.page

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.PageRepository
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.NOTEBOOK_ID
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.OPERATION
import com.labs.devo.apps.myshop.view.activity.notebook.NotebookActivity.NotebookConstants.PAGE
import com.labs.devo.apps.myshop.view.activity.notebook.page.AddEditPageViewModel.PageEventConstants.PAGE_ADDED
import com.labs.devo.apps.myshop.view.activity.notebook.page.AddEditPageViewModel.PageEventConstants.PAGE_DELETED
import com.labs.devo.apps.myshop.view.activity.notebook.page.AddEditPageViewModel.PageEventConstants.PAGE_NAME_NOT_UPDATED_ERR
import com.labs.devo.apps.myshop.view.activity.notebook.page.AddEditPageViewModel.PageEventConstants.PAGE_UPDATED
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.launch

class AddEditPageViewModel @ViewModelInject
constructor(
    private val pageRepository: PageRepository,
    @Assisted val state: SavedStateHandle
) :
    BaseViewModel<AddEditPageViewModel.AddEditPageEvent>() {

    val operation = state.get<String>(OPERATION)

    val page = state.get<Page>(PAGE)

    val notebookId = state.get<String>(NOTEBOOK_ID)


    fun addPage(page: Page) = viewModelScope.launch {
        val res = pageRepository.insertPage(page)
        res.data?.let {
            channel.send(AddEditPageEvent.PageInserted(PAGE_ADDED))
        }
            ?: channel.send(AddEditPageEvent.ShowInvalidInputMessage(res.message?.getContentIfNotHandled()))
    }

    fun updatePage(prevPage: Page, page: Page) = viewModelScope.launch {
        if (page.pageName == prevPage.pageName) {
            channel.send(AddEditPageEvent.ShowInvalidInputMessage(PAGE_NAME_NOT_UPDATED_ERR))
            return@launch
        }
        val data = pageRepository.updatePage(page)
        data.data?.let {
            channel.send(AddEditPageEvent.PageUpdated(PAGE_UPDATED))
        }
            ?: channel.send(
                AddEditPageEvent.ShowInvalidInputMessage(
                    data.message?.getContentIfNotHandled()
                )
            )
    }

    fun deletePage(page: Page) = viewModelScope.launch {
        val data = pageRepository.deletePage(page)
        data.data?.let {
            channel.send(AddEditPageEvent.PageDeleted(PAGE_DELETED))
        }
            ?: channel.send(
                AddEditPageEvent.ShowInvalidInputMessage(
                    data.message?.getContentIfNotHandled()
                )
            )
    }

    sealed class AddEditPageEvent {

        data class ShowInvalidInputMessage(val msg: String?) : AddEditPageEvent()

        data class PageInserted(val msg: String) : AddEditPageEvent()

        data class PageUpdated(val msg: String) :
            AddEditPageEvent()

        data class PageDeleted(val msg: String) :
            AddEditPageEvent()
    }

    object PageEventConstants {
        const val PAGE_DELETED = "Page Deleted"
        const val PAGE_UPDATED = "Page Updated"
        const val PAGE_ADDED = "Page added"
        const val PAGE_NAME_NOT_UPDATED_ERR = "Page's name is not changed"
    }
}