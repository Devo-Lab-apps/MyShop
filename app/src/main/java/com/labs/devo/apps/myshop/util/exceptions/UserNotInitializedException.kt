package com.labs.devo.apps.myshop.util.exceptions

import java.lang.RuntimeException

data class UserNotInitializedException(val msg: String = "User not initialized. Please go to home and retry."): RuntimeException(msg)