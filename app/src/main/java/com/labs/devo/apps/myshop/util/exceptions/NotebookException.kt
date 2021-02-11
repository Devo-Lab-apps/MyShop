package com.labs.devo.apps.myshop.util.exceptions

import java.lang.RuntimeException

data class NotebookLimitExceededException(val msg: String): RuntimeException(msg)

data class NotebookNotFoundException(val msg: String = "The notebook is not present and maybe deleted by another user."): RuntimeException(msg)