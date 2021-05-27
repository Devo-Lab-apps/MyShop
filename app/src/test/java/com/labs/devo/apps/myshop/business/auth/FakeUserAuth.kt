package com.labs.devo.apps.myshop.business.auth

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.labs.devo.apps.myshop.business.auth.abstraction.UserAuth
import com.labs.devo.apps.myshop.const.ErrorCode
import com.labs.devo.apps.myshop.const.ErrorMessages.UNKNOWN_ERROR_OCCURRED
import com.labs.devo.apps.myshop.data.models.account.User
import com.labs.devo.apps.myshop.data.models.auth.AuthenticationResult
import com.labs.devo.apps.myshop.data.models.auth.LoginUserCredentials
import com.labs.devo.apps.myshop.data.models.auth.SignUpUserCredentials
import com.labs.devo.apps.myshop.util.exceptions.ExceptionCatcher
import com.labs.devo.apps.myshop.view.util.DataState

class FakeUserAuth : UserAuth {

    var shouldReturnAuthError = true

    var shouldReturnDatabaseError = true

    override suspend fun login(credentials: LoginUserCredentials): DataState<AuthenticationResult> {
        if (shouldReturnAuthError) {
            return DataState.message(
                ExceptionCatcher.handleExceptionAndReturnErrorMessage(
                    ExceptionCatcher.GenericException(ErrorCode.ERROR_UNAUTHENTICATED)
                )
            )
        }
        if (shouldReturnDatabaseError) {
            return DataState.message(
                ExceptionCatcher.handleExceptionAndReturnErrorMessage(
                    ExceptionCatcher.GenericException(ErrorCode.ERROR_UNKNOWN_STATE, "Unable to connect to DB")
                )
            )
        }
        return DataState.data(AuthenticationResult.LoggedIn(User("123")), "User logged in")
    }


    override suspend fun signup(credentials: SignUpUserCredentials): DataState<AuthenticationResult> {
        if (shouldReturnAuthError) {
            return DataState.message(
                ExceptionCatcher.handleExceptionAndReturnErrorMessage(
                    ExceptionCatcher.GenericException(ErrorCode.ERROR_UNAUTHENTICATED)
                )
            )
        }
        if (shouldReturnDatabaseError) {
            return DataState.message(
                ExceptionCatcher.handleExceptionAndReturnErrorMessage(
                    ExceptionCatcher.GenericException(ErrorCode.ERROR_UNKNOWN_STATE, "Unable to connect to DB")
                )
            )
        }
        return DataState.data(AuthenticationResult.SignedUp(User("123")), "User signed up")
    }

    override suspend fun logoutOfAllDevices(credentials: LoginUserCredentials): DataState<AuthenticationResult> {
        if (shouldReturnAuthError) {
            return DataState.message(
                ExceptionCatcher.handleExceptionAndReturnErrorMessage(
                    ExceptionCatcher.GenericException(ErrorCode.ERROR_UNAUTHENTICATED)
                )
            )
        }
        if (shouldReturnDatabaseError) {
            return DataState.message(
                ExceptionCatcher.handleExceptionAndReturnErrorMessage(
                    ExceptionCatcher.GenericException(ErrorCode.ERROR_UNKNOWN_STATE)
                )
            )
        }
        return DataState.data(AuthenticationResult.LoggedOutOfDevices)
    }
}