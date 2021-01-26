package com.labs.devo.apps.myshop.data.db.remote.abstraction.account

import com.labs.devo.apps.myshop.data.models.account.User
import kotlinx.coroutines.flow.Flow

interface UserService {

    suspend fun getUser(email: String): Flow<User>


}