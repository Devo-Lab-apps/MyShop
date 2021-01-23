package com.labs.devo.apps.myshop.business.auth.implementation

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.labs.devo.apps.myshop.business.auth.abstraction.UserAuth
import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.auth.AuthenticationResult
import com.labs.devo.apps.myshop.data.models.auth.LoginUserCredentials
import com.labs.devo.apps.myshop.data.models.auth.SignUpUserCredentials
import com.labs.devo.apps.myshop.data.models.account.User
import com.labs.devo.apps.myshop.util.printLogD
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


/**
 * Implementation class for handling user authentication.
 */
class FirebaseUserAuth @Inject constructor(val auth: FirebaseAuth) :
    UserAuth {

    /**
     * Enum to decide the authentication event i.e. sign up or login.
     */
    enum class AuthEvent {
        LOGIN,
        SIGNUP
    }

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    override suspend fun loginUser(credentials: LoginUserCredentials): DataState<AuthenticationResult> {
        return try {
            val authResult =
                auth.signInWithEmailAndPassword(credentials.email, credentials.password).await()
            val isEmailVerified = auth.currentUser?.isEmailVerified
            return if (isEmailVerified == true) getUserFromDb(credentials.email, authResult)
            else {
                auth.signOut()
                throw java.lang.Exception("Please verify your email before login.")
            }
        } catch (e: Exception) {
            DataState.message(e.message ?: "An error occurred. Please try again later")
        }
    }


    override suspend fun signUpUser(credentials: SignUpUserCredentials): DataState<AuthenticationResult> {
        return try {
            val authResult =
                auth.createUserWithEmailAndPassword(credentials.email, credentials.password).await()
            createUserInDb(credentials.email, authResult, AuthEvent.SIGNUP)
        } catch (e: Exception) {
            DataState.message(e.message ?: "An error occurred. Please try again later")
        }
    }

    /**
     * Method to fetch user from DB that is logged in.
     */
    private suspend fun getUserFromDb(
        email: String,
        authResult: AuthResult?
    ): DataState<AuthenticationResult> {
        var user: User? = null
        try {
            val doc = FirebaseHelper.getUsersDocReference(email).get().await()
            user = doc.toObject(User::class.java)
            return DataState.data(
                data = AuthenticationResult(user!!),
                message = "Login Successful"
            )
        } catch (ex: Exception) {
            if (user == null) {
                printLogD(TAG, "Can't find user in db. So creating user.")

                //if user don't get created on sign up anyhow or deleted and it is a valid authenticated user then create user and return that.
                return createUserInDb(email, authResult, AuthEvent.LOGIN)
            }
            return DataState.data(
                data = AuthenticationResult(user),
                message = "Logged in"
            )
        }
    }


    /**
     * Method to created user in DB under "users" collection.
     */
    private suspend fun createUserInDb(
        email: String,
        authResult: AuthResult?,
        authEvent: AuthEvent
    ): DataState<AuthenticationResult> {
        var user: User? = null
        try {
            authResult?.let { res ->
                res.user?.let { u ->
                    user = User(
                        u.uid,
                        "", //not set on sign up
                        email,
                        System.currentTimeMillis()
                    )
                    FirebaseHelper.getUsersDocReference(email).set(user!!).await()
                    if (authEvent == AuthEvent.SIGNUP) {
                        u.sendEmailVerification()
                    }
                }
            }
            return if (authEvent == AuthEvent.LOGIN) {
                //very rare case
                DataState.data(
                    data = AuthenticationResult(user!!),
                    message = "Login Successful. Your profile data is reset. You have to fill it again. Sorry for inconvenience." //user should know to reset the profile.
                )
            } else {
                DataState.data(
                    data = AuthenticationResult(user!!),
                    message = "Successfully created. Please verify your email before login."
                )
            }
        } catch (ex: Exception) {
            user = null //to delete the user in finally make it null here.
            if (ex is FirebaseFirestoreException) {
                if (authEvent == AuthEvent.LOGIN) {
                    throw Exception(
                        "There's an error on our side. Please try again later. Sorry for inconvenience.",
                        ex
                    )
                } else {
                    throw Exception(
                        "An error occurred while creating user. Please try again later.",
                        ex
                    )
                }
            }
            throw ex
        } finally {
            //if signing up and an error occurred
            if (user == null && authEvent == AuthEvent.SIGNUP) {
                printLogD(TAG, "Can't store data in db. So deleting user")
                auth.currentUser?.delete()?.await()

                //sign out after creating the user to re-login.
                auth.signOut()
            }
            //sign out after anyway.
            auth.signOut()
        }
    }

}