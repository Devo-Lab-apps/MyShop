package com.labs.devo.apps.myshop.data.models.auth

data class LoginUserCredentials(
    val email: String,
    val password: String
)

data class SignUpUserCredentials(
    val email: String,
    val password: String,
    val confirmPassword: String
)