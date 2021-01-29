package com.labs.devo.apps.myshop.view.activity.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.business.account.abstraction.AccountRepository
import com.labs.devo.apps.myshop.business.account.abstraction.UserRepository
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.account.User
import com.labs.devo.apps.myshop.util.AppData
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    val userRepository: UserRepository,
    val accountRepository: AccountRepository
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


    sealed class HomeViewModelEvent {

        class UserChanged(val user: User) : HomeViewModelEvent()

        object LogoutUser : HomeViewModelEvent()

        object UserNotFound : HomeViewModelEvent()

    }
}