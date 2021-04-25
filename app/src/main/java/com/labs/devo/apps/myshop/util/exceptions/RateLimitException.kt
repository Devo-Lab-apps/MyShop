package com.labs.devo.apps.myshop.util.exceptions

import java.lang.RuntimeException

class OperationLimitExceedException(val operation: String): RuntimeException(
    "The operation $operation limit is exceeded for the day."
)

class TimeChangedException(): RuntimeException(
    "Please make date and time equal to network time of your time zone."
)