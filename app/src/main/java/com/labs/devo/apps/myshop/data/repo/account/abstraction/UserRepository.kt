package com.labs.devo.apps.myshop.data.repo.account.abstraction

import com.labs.devo.apps.myshop.data.models.account.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun getUser(email: String): Flow<User?>
}