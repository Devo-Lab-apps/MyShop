package com.labs.devo.apps.myshop.business.account.implementation

import com.labs.devo.apps.myshop.business.account.abstraction.AccountRepository
import com.labs.devo.apps.myshop.data.db.remote.abstraction.account.AccountService
import com.labs.devo.apps.myshop.data.models.account.Account
import com.labs.devo.apps.myshop.view.util.DataState
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    val accountService: AccountService
) : AccountRepository {

    override suspend fun getAccount(accountId: String): DataState<Account> =
        accountService.getAccount(accountId)


}