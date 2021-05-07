package com.labs.devo.apps.myshop.util

object StringUtils {

    fun containsSpecialChars(vararg strs: String): Boolean {
        return strs.any { it.matches(Regex("[\$&+,:;=\\\\\\\\?@#|/'<>.^*()%!-]")) }
    }
}