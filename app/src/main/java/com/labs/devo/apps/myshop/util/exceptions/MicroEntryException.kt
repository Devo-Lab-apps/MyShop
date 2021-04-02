package com.labs.devo.apps.myshop.util.exceptions

import java.lang.RuntimeException


data class NoMicroEntryException(val msg: String = "No micro entries found to be inserted"): RuntimeException(msg)

data class MicroEntryNotFoundException(val msg: String = "Micro Entry not found."): RuntimeException(msg)

data class MicroEntryLimitExceededException(val msg: String = "You can't have more than 2 micro entries per page.") :
    RuntimeException(msg)