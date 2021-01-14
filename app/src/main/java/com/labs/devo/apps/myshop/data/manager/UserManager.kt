package com.labs.devo.apps.myshop.data.manager

import com.labs.devo.apps.myshop.data.models.auth.User


object UserManager {
    private var user: User? = null
    fun initUser(user: User) {
        this.user = user
    }
}
