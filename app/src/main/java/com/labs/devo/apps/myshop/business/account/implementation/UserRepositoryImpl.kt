package com.labs.devo.apps.myshop.business.account.implementation

import com.labs.devo.apps.myshop.business.account.abstraction.UserRepository
import com.labs.devo.apps.myshop.data.db.remote.abstraction.account.UserService
import com.labs.devo.apps.myshop.data.models.account.User
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl constructor(val userService: UserService) : UserRepository {

    override suspend fun getUser(email: String): Flow<User> = userService.getUser(email)
}