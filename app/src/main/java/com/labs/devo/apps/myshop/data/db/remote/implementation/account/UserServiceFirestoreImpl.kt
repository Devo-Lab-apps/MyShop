package com.labs.devo.apps.myshop.data.db.remote.implementation.account

import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.db.remote.abstraction.account.UserService
import com.labs.devo.apps.myshop.data.models.account.User
import com.labs.devo.apps.myshop.util.printLogD
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class UserServiceFirestoreImpl : UserService {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    override suspend fun getUser(email: String): Flow<User?> {
        return channelFlow {
            val listener =
                FirebaseHelper.getUsersDocReference(email).addSnapshotListener { ds, ex ->
                    if (ex != null) {
                        cancel(message = "Error fetching data for user with email: $email", ex)
                        return@addSnapshotListener
                    }
                    if (ds != null && ds.exists()) {
                        val user = ds.toObject(User::class.java)
                        offer(user)
                    } else {
                        offer(null)
                    }
                }
            awaitClose {
                listener.remove()
            }
        }
    }

}