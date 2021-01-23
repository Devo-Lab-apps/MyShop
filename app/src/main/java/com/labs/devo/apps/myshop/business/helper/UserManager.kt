package com.labs.devo.apps.myshop.business.helper

import com.labs.devo.apps.myshop.data.models.account.User

/**
 * Manager object to initialize in memory user.
 */
object UserManager {
    /**
     * Store user in memory.
     */
    private var user: User? = null

    /**
     * Initialize user.
     */
    fun initUser(user: User?) {
        UserManager.user = user
    }
}
