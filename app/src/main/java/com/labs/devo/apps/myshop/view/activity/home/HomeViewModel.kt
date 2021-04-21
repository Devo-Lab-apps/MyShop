package com.labs.devo.apps.myshop.view.activity.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.data.repo.account.abstraction.AccountRepository
import com.labs.devo.apps.myshop.data.repo.account.abstraction.UserRepository
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.account.User
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.EntryRepository
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.NotebookRepository
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.PageRepository
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.RecurringEntryRepository
import com.labs.devo.apps.myshop.util.AppData
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val notebookRepository: NotebookRepository,
    private val pageRepository: PageRepository,
    private val entryRepository: EntryRepository,
    private val recurringEntryRepository: RecurringEntryRepository,

    ) : BaseViewModel<HomeViewModel.HomeViewModelEvent>() {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName


    fun attachSnapshotToUser(email: String) = viewModelScope.launch {
        userRepository.getUser(email).collect { user ->
            if (user == null) {
                //TODO add retry mechanism

                channel.send(HomeViewModelEvent.UserNotFound)
                return@collect
            }
            UserManager.initUser(user)
            if (user.loggedInDeviceId != AppData.deviceId) {
                channel.send(HomeViewModelEvent.LogoutUser)
            }
        }
    }

    suspend fun deleteAllLocalData() = viewModelScope.launch {
        entryRepository.deleteEntries()
        recurringEntryRepository.deleteRecurringEntries()
        pageRepository.deletePages()
        notebookRepository.deleteNotebooks()
    }


    sealed class HomeViewModelEvent {

        object DataCleared: HomeViewModelEvent()

        object LogoutUser : HomeViewModelEvent()

        object UserNotFound : HomeViewModelEvent()

    }
}