package com.labs.devo.apps.myshop.view.activity.notebook.page

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.business.notebook.abstraction.PageRepository
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.launch

class AddEditPageViewModel @ViewModelInject
constructor(private val pageRepository: PageRepository) :
    BaseViewModel<AddEditPageViewModel.AddEditPageEvent>() {


    fun addPage(page: Page) = viewModelScope.launch {
        val res = pageRepository.insertPage(page)
        res.data?.let {
            channel.send(AddEditPageEvent.PageInserted("Page inserted"))
        }
            ?: channel.send(AddEditPageEvent.ShowInvalidInputMessage(res.message?.getContentIfNotHandled()))
    }

    fun updatePage(prevPage: Page, page: Page) = viewModelScope.launch {
        if (page.pageName == prevPage.pageName) {
            channel.send(AddEditPageEvent.ShowInvalidInputMessage("Page's name is not changed"))
            return@launch
        }
        val data = pageRepository.updatePage(page)
        data.data?.let {
            channel.send(AddEditPageEvent.PageUpdated("Page is updated"))
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
            channel.send(AddEditPageEvent.PageDeleted("Page is deleted"))
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
}