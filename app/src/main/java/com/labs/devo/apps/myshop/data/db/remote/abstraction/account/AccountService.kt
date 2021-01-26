package com.labs.devo.apps.myshop.data.db.remote.abstraction.account

import com.labs.devo.apps.myshop.data.models.account.Account
import com.labs.devo.apps.myshop.view.util.DataState

interface AccountService {

    suspend fun getAccount(accountId: String): DataState<Account>

}