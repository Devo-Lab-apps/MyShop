package com.labs.devo.apps.myshop.business.helper

import com.labs.devo.apps.myshop.data.models.account.User

/**
 * Manager object to initialize in memory user.
 */
object UserManager {
    /**
     * Store user in memory.
     */
    var user: User? = null
        private set

    /**
     * Initialize user.
     */
    fun initUser(user: User?) {
        UserManager.user = user
    }
}
