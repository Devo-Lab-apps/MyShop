package com.labs.devo.apps.myshop.data.models.account

/**
 * User class that stores the profile of the user.
 */
data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val loggedInAt: Long = 0
    //TODO add other profile attributes
)