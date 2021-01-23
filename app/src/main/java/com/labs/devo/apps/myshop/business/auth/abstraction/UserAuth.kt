package com.labs.devo.apps.myshop.business.auth.abstraction

import com.labs.devo.apps.myshop.data.models.auth.AuthenticationResult
import com.labs.devo.apps.myshop.data.models.auth.LoginUserCredentials
import com.labs.devo.apps.myshop.data.models.auth.SignUpUserCredentials
import com.labs.devo.apps.myshop.view.util.DataState

/**
 * Interface to handle authentication for the user.
 */
interface UserAuth {

    /**
     * Method to login user.
     */
    suspend fun loginUser(credentials: LoginUserCredentials): DataState<AuthenticationResult>

    /**
     * Method to create new user.
     */
    suspend fun signUpUser(credentials: SignUpUserCredentials): DataState<AuthenticationResult>
}