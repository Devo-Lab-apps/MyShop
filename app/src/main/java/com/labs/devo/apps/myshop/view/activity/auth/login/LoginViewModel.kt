package com.labs.devo.apps.myshop.view.activity.auth.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.business.auth.abstraction.UserAuth
import com.labs.devo.apps.myshop.data.manager.UserManager
import com.labs.devo.apps.myshop.data.models.auth.LoginUserCredentials
import com.labs.devo.apps.myshop.data.models.auth.User
import com.labs.devo.apps.myshop.helper.extensions.isValidEmail
import com.labs.devo.apps.myshop.helper.extensions.isValidPassword
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.launch

class LoginViewModel @ViewModelInject constructor(val userAuth: UserAuth) :
    BaseViewModel<LoginViewModel.LoginEvent>() {


    fun loginUser(credentials: LoginUserCredentials) {
        if (!credentials.email.isValidEmail()) {
            showInvalidInputMessage("Invalid email entered")
            return
        }
        if (!credentials.password.isValidPassword()) {
            showInvalidInputMessage("Password must be of length greater than 6")
            return
        }

        signUp(credentials)
    }

    private fun signUp(credentials: LoginUserCredentials) = viewModelScope.launch {
        val data = userAuth.loginUser(credentials)
        data.data?.let { res ->
            val authResult = res.getContentIfNotHandled()
            channel.send(
                LoginEvent.UserLoggedIn(
                    data.message?.getContentIfNotHandled() ?: "Successfully authenticated"
                )
            )
            initUser(authResult?.user!!)
        } ?: showInvalidInputMessage(data.message?.getContentIfNotHandled()!!)
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        channel.send(LoginEvent.ShowInvalidInputMessage(text))
    }

    private fun initUser(user: User) {
        UserManager.initUser(user)
    }

    sealed class LoginEvent {
        data class ShowInvalidInputMessage(val msg: String) : LoginEvent()
        data class UserLoggedIn(val msg: String) : LoginEvent()
    }
}