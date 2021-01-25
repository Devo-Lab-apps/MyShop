package com.labs.devo.apps.myshop.data.models.auth

import com.labs.devo.apps.myshop.data.models.account.User

/**
 * Class to hold the result of authentication.
 */
sealed class AuthenticationResult {
    data class LoggedIn(val user: User) : AuthenticationResult()

    data class SignedUp(val user: User) : AuthenticationResult()

    object LogoutOfDevicesError : AuthenticationResult()

    object LoggedOutOfDevices : AuthenticationResult()
}