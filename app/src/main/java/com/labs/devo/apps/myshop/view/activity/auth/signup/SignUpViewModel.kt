package com.labs.devo.apps.myshop.view.activity.auth.signup

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.labs.devo.apps.myshop.business.auth.abstraction.UserAuth
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.data.models.auth.SignUpUserCredentials
import com.labs.devo.apps.myshop.data.models.account.User
import com.labs.devo.apps.myshop.data.models.auth.AuthenticationResult
import com.labs.devo.apps.myshop.util.extensions.isValidEmail
import com.labs.devo.apps.myshop.util.extensions.isValidPassword
import com.labs.devo.apps.myshop.view.util.BaseViewModel
import kotlinx.coroutines.launch

class SignUpViewModel
@ViewModelInject constructor(private val userAuth: UserAuth) :
    BaseViewModel<SignUpViewModel.SignUpEvent>() {


    fun signUpUser(credentials: SignUpUserCredentials) {
        if (!credentials.email.isValidEmail()) {
            showInvalidInputMessage("Invalid email entered")
            return
        }
        if (!credentials.password.isValidPassword()) {
            showInvalidInputMessage("Password must be of length greater than 6")
            return
        }

        if (credentials.password != credentials.confirmPassword) {
            showInvalidInputMessage("Passwords don't match")
            return
        }

        signUp(credentials)
    }

    private fun signUp(credentials: SignUpUserCredentials) = viewModelScope.launch {
        val data = userAuth.signUpUser(credentials)
        data.data?.let { res ->
            val authResult = res.getContentIfNotHandled()
            channel.send(
                SignUpEvent.UserSignedUp(
                    data.message?.getContentIfNotHandled()
                )
            )
            if (authResult is AuthenticationResult.SignedUp) {
                initUser(authResult.user)
            }
        } ?: showInvalidInputMessage(data.message?.getContentIfNotHandled())
    }

    private fun showInvalidInputMessage(text: String?) = viewModelScope.launch {
        channel.send(SignUpEvent.ShowInvalidInputMessage(text))
    }

    private fun initUser(user: User?) {
        UserManager.initUser(user)
    }

    sealed class SignUpEvent {
        data class ShowInvalidInputMessage(val msg: String?) : SignUpEvent()
        data class UserSignedUp(val msg: String?) : SignUpEvent()
    }
}