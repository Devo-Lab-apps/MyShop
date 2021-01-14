package com.labs.devo.apps.myshop.data.models.auth

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val loggedInAt: Long = 0,
)