package com.labs.devo.apps.myshop.util.exceptions

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.labs.devo.apps.myshop.const.ErrorCode
import com.labs.devo.apps.myshop.const.ErrorMessages.UNKNOWN_ERROR_OCCURRED_RETRY
import com.labs.devo.apps.myshop.util.cLog

object ExceptionCatcher {

    fun decodeFirebaseAuthException(errorCode: String): String {
        return when (ErrorCode.valueOf(errorCode)) {
            ErrorCode.ERROR_INVALID_EMAIL -> "Your email address appears to be malformed."
            ErrorCode.ERROR_WRONG_PASSWORD -> "Your password is wrong."
            ErrorCode.ERROR_USER_NOT_FOUND -> "User with this email doesn't exist."
            ErrorCode.ERROR_USER_DISABLED -> "User with this email has been disabled."
            ErrorCode.ERROR_TOO_MANY_REQUESTS -> "Too many requests. Try again later."
            else -> UNKNOWN_ERROR_OCCURRED_RETRY
        }
    }

    fun decodeFirebaseFirestoreException(errorCode: String): String {
        return when (ErrorCode.valueOf(errorCode)) {
            ErrorCode.ERROR_NOT_FOUND -> UNKNOWN_ERROR_OCCURRED_RETRY
            ErrorCode.ERROR_ALREADY_EXISTS -> UNKNOWN_ERROR_OCCURRED_RETRY
            ErrorCode.ERROR_PERMISSION_DENIED -> "You are allowed to perform this operation."
            ErrorCode.ERROR_RESOURCE_EXHAUSTED -> UNKNOWN_ERROR_OCCURRED_RETRY
            ErrorCode.ERROR_UNAUTHENTICATED -> "You are not logged in."
            else -> UNKNOWN_ERROR_OCCURRED_RETRY
        }
    }


    fun handleExceptionAndReturnErrorMessage(e: Exception): String {
        return when (e) {
            is FirebaseFirestoreException -> decodeFirebaseFirestoreException(e.code.name)
            is FirebaseAuthException -> decodeFirebaseFirestoreException(e.errorCode)
            is GenericException -> decodeGenericException(e)
            else -> e.message ?: UNKNOWN_ERROR_OCCURRED_RETRY
        }
    }

    private fun decodeGenericException(ex: GenericException): String {
        return when (ex.errorCode) {
            ErrorCode.ERROR_EMAIL_NOT_VERIFIED -> "Please verify your email before login."
            ErrorCode.ERROR_AUTHENTICATED_USER_NOT_FOUND -> "Something went wrong on our side. Please contact the customer care."
            ErrorCode.ERROR_UNKNOWN_STATE -> {
                if (ex.reason != null) cLog(ex.reason)
                UNKNOWN_ERROR_OCCURRED_RETRY
            }
            else -> UNKNOWN_ERROR_OCCURRED_RETRY
        }
    }

    data class GenericException(val errorCode: ErrorCode, val reason: String? = null) : Exception()

}