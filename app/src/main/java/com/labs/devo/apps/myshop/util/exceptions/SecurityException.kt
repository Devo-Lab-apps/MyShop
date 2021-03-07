package com.labs.devo.apps.myshop.util.exceptions

import java.lang.RuntimeException

class PermissionRequiredException(permission: String, val msg: String = "User don't have $permission Permission."): RuntimeException(msg)