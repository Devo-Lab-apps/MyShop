package com.labs.devo.apps.myshop.data.db.remote.implementation.account

import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.data.db.remote.abstraction.account.AccountService
import com.labs.devo.apps.myshop.data.models.account.Account
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.tasks.await

class AccountServiceFirestoreImpl : AccountService {

    override suspend fun getAccount(accountId: String): DataState<Account> {
        return try {
            val doc = FirebaseHelper.getAccountDocumentReference(accountId).get().await()
            val account = doc.toObject(Account::class.java)!!
            return DataState.data(
                data = account
            )
        } catch (ex: Exception) {
            DataState.message("Can't get the user for the user.")
        }
    }
}