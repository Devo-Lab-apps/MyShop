package com.labs.devo.apps.myshop.util.extensions


/**
 * Validate an email.
 */
fun String.isValidEmail(): Boolean {
    val pattern =
        "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
    return this.matches(Regex(pattern))
}

/**
 * Validate an email.
 */
fun String?.isValidPhone(): Boolean {
    if (this.isNullOrEmpty()) return true
    return (this.matches(Regex("^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*\$")))
}

/**
 * Validate a password.
 */
fun String.isValidPassword(): Boolean {
    val pwd = this
    return !(pwd.isBlank() || pwd.length < 6)
}

fun specialCharsNotAllowedMsg(str: String) = "Special characters are not allowed in ${str}}"