package com.labs.devo.apps.myshop.data.repo.account.abstraction

import com.labs.devo.apps.myshop.data.models.account.Account
import com.labs.devo.apps.myshop.view.util.DataState

interface AccountRepository {


    suspend fun getAccount(accountId: String): DataState<Account>

}