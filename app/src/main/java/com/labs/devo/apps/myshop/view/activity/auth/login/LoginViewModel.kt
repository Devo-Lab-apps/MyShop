package com.labs.devo.apps.myshop.view.activity.auth.login

import androidx.hilt.lifecycle.ViewModelInject
import com.labs.devo.apps.myshop.view.util.BaseViewModel

class LoginViewModel @ViewModelInject constructor() : BaseViewModel<LoginViewModel.LoginEvent>() {


    sealed class LoginEvent {
        data class UserLoggedIn(val msg: String) : LoginEvent()
    }
}