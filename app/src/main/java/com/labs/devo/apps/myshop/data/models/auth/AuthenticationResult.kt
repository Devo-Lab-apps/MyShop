package com.labs.devo.apps.myshop.data.models.auth

import com.labs.devo.apps.myshop.data.models.account.User

/**
 * Class to hold the result of authentication.
 */
data class AuthenticationResult (
    val user: User
)