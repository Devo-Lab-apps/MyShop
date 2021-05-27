package com.labs.devo.apps.myshop.util.exceptions

import com.labs.devo.apps.myshop.const.ErrorCode

data class ItemDetailNotFoundException(val code: ErrorCode, val string: String? = null) :
    ExceptionCatcher.GenericException(code, string)