package com.labs.devo.apps.myshop.util.exceptions

import java.lang.RuntimeException

data class PageNotFoundException(val msg: String): RuntimeException(msg)
