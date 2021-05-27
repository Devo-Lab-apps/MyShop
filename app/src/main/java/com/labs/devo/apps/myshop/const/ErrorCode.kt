package com.labs.devo.apps.myshop.const

enum class ErrorCode {
    //Firebase auth
    ERROR_INVALID_EMAIL,
    ERROR_USER_NOT_FOUND,
    ERROR_WRONG_PASSWORD,
    ERROR_USER_DISABLED,
    ERROR_TOO_MANY_REQUESTS,
    ERROR_EMAIL_NOT_VERIFIED,


    //Firestore exceptions
    ERROR_PERMISSION_DENIED,
    ERROR_NOT_FOUND,
    ERROR_ALREADY_EXISTS,
    ERROR_RESOURCE_EXHAUSTED,
    ERROR_UNAUTHENTICATED,

    //Miscellaneous errors
    ERROR_NO_INTERNET,
    ERROR_AUTHENTICATED_USER_NOT_FOUND, // when user is logged in and tries to get user but he is unable to
    ERROR_UNKNOWN_STATE,



    //Item detail
    ERROR_ITEM_DETAIL_NOT_FOUND
}