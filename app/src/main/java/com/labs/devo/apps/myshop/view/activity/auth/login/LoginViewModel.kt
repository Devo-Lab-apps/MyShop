package com.labs.devo.apps.myshop.view.activity.auth.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.business.auth.abstraction.UserAuth
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.data.models.account.User
import com.labs.devo.apps.myshop.data.models.auth.AuthenticationResult
import com.labs.devo.apps.myshop.data.models.auth.LoginUserCredentials
import com.labs.devo.apps.myshop.util.extensions.isValidEmail
import com.labs.devo.apps.myshop.util.extensions.isValidPassword
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.launch

class LoginViewModel @ViewModelInject constructor(private val userAuth: UserAuth) :
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

        login(credentials)
    }

    private fun login(credentials: LoginUserCredentials) = viewModelScope.launch {
        val data = userAuth.loginUser(credentials)
        data.data?.let { res ->
            val authResult = res.getContentIfNotHandled()
            if (authResult is AuthenticationResult.LoggedIn) {
                initUser(authResult.user)
                channel.send(
                    LoginEvent.UserLoggedIn(
                        data.message?.getContentIfNotHandled()
                    )
                )
            } else if (authResult is AuthenticationResult.LogoutOfDevicesError) {
                channel.send(
                    LoginEvent.LogoutOfAllDeviceError(
                        data.message?.getContentIfNotHandled()
                    )
                )
            }
        } ?: showInvalidInputMessage(data.message?.getContentIfNotHandled())
    }

    fun logoutOfAllDevices(credentials: LoginUserCredentials) = viewModelScope.launch {
        if (!credentials.email.isValidEmail()) {
            showInvalidInputMessage("Invalid email entered")
        }
        if (!credentials.password.isValidPassword()) {
            showInvalidInputMessage("Password must be of length greater than 6")
        } else {
            val data = userAuth.logoutOfAllDevices(credentials)
            data.data?.let { res ->
                val authResult = res.getContentIfNotHandled()
                if (authResult is AuthenticationResult.LoggedOutOfDevices) {
                    channel.send(
                        LoginEvent.LoggedOfAllDevices(
                            data.message?.getContentIfNotHandled()
                        )
                    )
                }
            } ?: showInvalidInputMessage(data.message?.getContentIfNotHandled())
        }
    }


    private fun showInvalidInputMessage(text: String?) = viewModelScope.launch {
        channel.send(LoginEvent.ShowInvalidInputMessage(text))
    }

    private fun initUser(user: User?) {
        UserManager.initUser(user)
    }


    sealed class LoginEvent {
        data class ShowInvalidInputMessage(val msg: String?) : LoginEvent()
        data class UserLoggedIn(val msg: String?) : LoginEvent()
        data class LogoutOfAllDeviceError(val msg: String?) : LoginEvent()
        data class LoggedOfAllDevices(val msg: String?) : LoginEvent()
    }
}