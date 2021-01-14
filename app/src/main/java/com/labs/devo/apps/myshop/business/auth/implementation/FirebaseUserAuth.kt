package com.labs.devo.apps.myshop.business.auth.implementation

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.labs.devo.apps.myshop.business.auth.abstraction.UserAuth
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.auth.AuthenticationResult
import com.labs.devo.apps.myshop.data.models.auth.LoginUserCredentials
import com.labs.devo.apps.myshop.data.models.auth.SignUpUserCredentials
import com.labs.devo.apps.myshop.data.models.auth.User
import com.labs.devo.apps.myshop.data.util.FirebaseUtil
import com.labs.devo.apps.myshop.util.printLogD
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseUserAuth @Inject constructor(val auth: FirebaseAuth, val db: FirebaseFirestore) :
    UserAuth {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    override suspend fun loginUser(credentials: LoginUserCredentials): DataState<AuthenticationResult> {
        TODO("Not yet implemented")
    }

    override suspend fun signUpUser(credentials: SignUpUserCredentials): DataState<AuthenticationResult> {
        return try {
            val authResult =
                auth.createUserWithEmailAndPassword(credentials.email, credentials.password).await()
            createUserInDb(credentials.email, authResult)
        } catch (e: Exception) {
            DataState.message(e.message ?: "An error occurred. Please try again later")
        }
    }

    private suspend fun createUserInDb(
        email: String,
        authResult: AuthResult?
    ): DataState<AuthenticationResult> {
        var user: User? = null
        try {
            authResult?.let { res ->
                printLogD(javaClass.name, res.user ?: "Null User")
                res.user?.let { u ->
                    user = User(
                        u.uid,
                        "",
                        email,
                        System.currentTimeMillis()
                    )
                    val docRef = FirebaseUtil.getUsersCollection().add(user!!).await()
                    printLogD(javaClass.name, docRef.path)
                }
            }
            return DataState.data(
                data = AuthenticationResult(user!!),
                message = "Successfully created"
            )
        } catch (ex: java.lang.Exception) {
            user = null
            throw ex
        } finally {
            if (user == null) {
                printLogD(TAG, "Can't store data in db. So deleting user")
                auth.currentUser?.delete()?.await()

                //sign out after creating the user to re-login.
                auth.signOut()
                return DataState.message("An error occurred. Please try again later")
            }
            //sign out after creating the user to re-login.
            auth.signOut()
        }
    }

}