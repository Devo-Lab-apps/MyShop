package com.labs.devo.apps.myshop.business.auth.implementation

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.labs.devo.apps.myshop.business.auth.abstraction.UserAuth
import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.account.Account
import com.labs.devo.apps.myshop.data.models.account.User
import com.labs.devo.apps.myshop.data.models.auth.AuthenticationResult
import com.labs.devo.apps.myshop.data.models.auth.LoginUserCredentials
import com.labs.devo.apps.myshop.data.models.auth.SignUpUserCredentials
import com.labs.devo.apps.myshop.util.AppData
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
            auth.signInWithEmailAndPassword(credentials.email, credentials.password).await()
            val isEmailVerified = auth.currentUser?.isEmailVerified
            return if (isEmailVerified == true) {
                getUserFromDb(credentials.email)
            } else {
                throw java.lang.Exception("Please verify your email before login.")
            }
        } catch (e: Exception) {
            auth.signOut()
            DataState.message(e.message ?: "An error occurred. Please try again later")
        }
    }


    override suspend fun signUpUser(credentials: SignUpUserCredentials): DataState<AuthenticationResult> {
        return try {
            val authResult =
                auth.createUserWithEmailAndPassword(credentials.email, credentials.password).await()
            val res = createAccountAndUserInDb(credentials.email, authResult)
            //sign out and return result
            auth.signOut()
            res
        } catch (e: Exception) {
            auth.signOut()
            DataState.message(e.message ?: "An error occurred. Please try again later")
        }
    }

    override suspend fun logoutOfAllDevices(credentials: LoginUserCredentials): DataState<AuthenticationResult> {
        return try {
            auth.signInWithEmailAndPassword(credentials.email, credentials.password).await()
            val doc = FirebaseHelper.getUsersDocReference(credentials.email).get().await()
            val user = doc.toObject(User::class.java)!!
            clearLoginDevice(user)
            //for re-login
            auth.signOut()
            DataState.data(
                data = AuthenticationResult.LoggedOutOfDevices,
                message = "Logged out of all devices. Please login again."
            )
        } catch (ex: java.lang.Exception) {
            auth.signOut()
            if (ex is FirebaseFirestoreException || ex is NullPointerException) {
                throw Exception(
                    "An error occurred while creating user. Please try again later.",
                    ex
                )
            }
            throw ex
        }
    }


    /**
     * Method to fetch user from DB that is logged in.
     */
    private suspend fun getUserFromDb(
        email: String
    ): DataState<AuthenticationResult> {
        val user: User?
        try {
            val doc = FirebaseHelper.getUsersDocReference(email).get().await()
            user = doc.toObject(User::class.java)!!
            //if first time login, same device login or logged out of all devices.
            if (user.loggedInDevice == "" || user.loggedInDevice == AppData.deviceId) {
                updateLoginTimeAndLoginDevice(user)
                //if logging in from another device
            } else if (AppData.deviceId != user.loggedInDevice) {
                auth.signOut()
                return DataState.data(
                    data = AuthenticationResult.LogoutOfDevicesError,
                    message = "Please logout of all devices before login."
                )
            }
            return DataState.data(
                data = AuthenticationResult.LoggedIn(user),
                message = "Login Successful"
            )
        } catch (ex: Exception) {
            //TODO decide whether to create account and user in db.
            //TODO create a customer care
            throw java.lang.Exception("Can't find user record. Please contact with our customer care.")
        }
    }


    /**
     * Method to created user in DB under "user" and "account" collection.
     */
    private suspend fun createAccountAndUserInDb(
        email: String,
        authResult: AuthResult?
    ): DataState<AuthenticationResult> {
        var user: User? = null
        return try {
            authResult?.let { res ->
                res.user?.let { u ->
                    val account = createAccountInDb(email)
                    user = createUserInDb(email, account.accountId, u.uid)
                    u.sendEmailVerification()
                }
            }
            DataState.data(
                data = AuthenticationResult.SignedUp(user!!),
                message = "Successfully created. Please verify your email before login."
            )
        } catch (ex: Exception) {
            user = null //to delete the user in finally make it null here.
            printLogD(TAG, "Can't store data in db. So deleting user")
            auth.currentUser?.delete()?.await()
            if (ex is FirebaseFirestoreException || ex is NullPointerException) {
                throw Exception(
                    "An error occurred while creating user. Please try again later.",
                    ex
                )
            }
            throw ex
        }
    }

    /**
     * Save user in db under user collection.
     */
    private suspend fun createUserInDb(email: String, accountId: String, uid: String): User {
        val user = User(
            uid,
            "", //not set on sign up
            email,
            accountId,
            signedUpInAt = System.currentTimeMillis(),
        )

        FirebaseHelper.getUsersDocReference(email).set(user).await()
        return user
    }

    /**
     * Save account in db for new account.
     */
    private suspend fun createAccountInDb(email: String): Account {
        val accountId = FirebaseHelper.getAccountDocumentReference().id

        val account = Account(
            accountId,
            listOf(email) //add user in list of users
        )

        FirebaseHelper.getAccountDocumentReference(accountId)
            .set(account).await()
        return account
    }

    /**
     * Set logged in time and device for the user.
     */
    private suspend fun updateLoginTimeAndLoginDevice(user: User) {
        val loggedInDeviceId = AppData.deviceId
        user.loggedInAt = System.currentTimeMillis()
        user.loggedInDevice = loggedInDeviceId
        FirebaseHelper.getUsersDocReference(user.email).set(user).await()
    }

    /**
     * Clean login device for the user.
     */
    private suspend fun clearLoginDevice(user: User) {
        user.loggedInDevice = ""
        FirebaseHelper.getUsersDocReference(user.email).set(user).await()
    }

}