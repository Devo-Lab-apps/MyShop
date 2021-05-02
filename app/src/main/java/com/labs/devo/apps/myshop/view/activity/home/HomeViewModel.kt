package com.labs.devo.apps.myshop.view.activity.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.local.database.dao.RecurringEntryDao
import com.labs.devo.apps.myshop.data.db.local.database.dao.RemoteKeyDao
import com.labs.devo.apps.myshop.data.repo.account.abstraction.UserRepository
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.EntryRepository
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.NotebookRepository
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.PageRepository
import com.labs.devo.apps.myshop.data.repo.notebook.abstraction.RecurringEntryRepository
import com.labs.devo.apps.myshop.util.AppData
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val notebookDao: NotebookRepository,
    private val pageDao: PageRepository,
    private val entryDao: EntryRepository,
    private val recurringEntryRepository: RecurringEntryRepository,
    private val recurringEntryDao: RecurringEntryDao,
    private val remoteDao: RemoteKeyDao,
) : BaseViewModel<HomeViewModel.HomeViewModelEvent>() {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    val entries = MutableStateFlow("").flatMapLatest { _ ->
        recurringEntryRepository.getRecurringEntries(
            null,
            false
        )
    }.cachedIn(viewModelScope)

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
        entryDao.deleteEntries()
        recurringEntryDao.deleteAll()
        pageDao.deletePages()
        notebookDao.deleteNotebooks()
        remoteDao.deleteAll()
        //TODO clean data store
    }

    sealed class HomeViewModelEvent {

        object DataCleared : HomeViewModelEvent()

        object LogoutUser : HomeViewModelEvent()

        object UserNotFound : HomeViewModelEvent()

    }
}