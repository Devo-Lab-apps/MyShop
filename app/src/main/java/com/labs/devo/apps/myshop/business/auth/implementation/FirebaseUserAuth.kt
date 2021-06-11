package com.labs.devo.apps.myshop.business.auth.implementation

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.labs.devo.apps.myshop.business.auth.abstraction.UserAuth
import com.labs.devo.apps.myshop.business.helper.FirebaseConstants
import com.labs.devo.apps.myshop.business.helper.FirebaseHelper
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.const.ErrorCode
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.ImportStatus
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.NotebookMetadataConstants
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.RemoteEntityNotebook
import com.labs.devo.apps.myshop.data.models.account.Account
import com.labs.devo.apps.myshop.data.models.account.User
import com.labs.devo.apps.myshop.data.models.auth.AuthenticationResult
import com.labs.devo.apps.myshop.data.models.auth.LoginUserCredentials
import com.labs.devo.apps.myshop.data.models.auth.SignUpUserCredentials
import com.labs.devo.apps.myshop.util.AppData
import com.labs.devo.apps.myshop.util.exceptions.ExceptionCatcher
import com.labs.devo.apps.myshop.util.exceptions.ExceptionCatcher.handleExceptionAndReturnErrorMessage
import com.labs.devo.apps.myshop.view.util.DataState
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


/**
 * Implementation class for handling user authentication.
 */
class FirebaseUserAuth @Inject constructor(val auth: FirebaseAuth) :
    UserAuth {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    override suspend fun login(credentials: LoginUserCredentials): DataState<AuthenticationResult> {
        return try {
            auth.signInWithEmailAndPassword(credentials.email, credentials.password).await()
            val isEmailVerified = auth.currentUser?.isEmailVerified
            return if (isEmailVerified == true) {
                getUserFromDb(credentials.email)
            } else {
                throw ExceptionCatcher.GenericException(ErrorCode.ERROR_EMAIL_NOT_VERIFIED)
            }
        } catch (ex: java.lang.Exception) {
            auth.signOut()
            DataState.message(handleExceptionAndReturnErrorMessage(ex))
        }
    }


    override suspend fun signup(credentials: SignUpUserCredentials): DataState<AuthenticationResult> {
        return try {
            val authResult =
                auth.createUserWithEmailAndPassword(credentials.email, credentials.password).await()
            createAccountAndUserInDb(credentials.email, authResult)
        } catch (ex: java.lang.Exception) {
            DataState.message(handleExceptionAndReturnErrorMessage(ex))
        } finally {
            auth.signOut()
        }
    }

    override suspend fun logoutOfAllDevices(credentials: LoginUserCredentials): DataState<AuthenticationResult> {
        return try {
            auth.signInWithEmailAndPassword(credentials.email, credentials.password).await()
            val doc = FirebaseHelper.getUsersDocReference(credentials.email).get().await()
            val user = doc.toObject(User::class.java)!!
            clearLoginDevice(user)
            DataState.data(AuthenticationResult.LoggedOutOfDevices)
        } catch (ex: java.lang.Exception) {
            DataState.message(handleExceptionAndReturnErrorMessage(ex))
        } finally {
            //for re-login
            auth.signOut()
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
            if (user.loggedInDeviceId == AppConstants.EMPTY_STRING || user.loggedInDeviceId == AppData.deviceId) {
                updateLoginTimeAndLoginDevice(user)
                //if logging in from another device
            } else if (AppData.deviceId != user.loggedInDeviceId) {
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
            throw ExceptionCatcher.GenericException(ErrorCode.ERROR_AUTHENTICATED_USER_NOT_FOUND)
        }
    }


    /**
     * Method to created user in DB under user and account collection.
     */
    private suspend fun createAccountAndUserInDb(
        email: String,
        authResult: AuthResult?
    ): DataState<AuthenticationResult> {
        return try {
            var user: User? = null
            authResult?.let { res ->
                res.user?.let { u ->
                    val account = createAccountInDb(email)
                    user = createUserInDb(email, account.accountId, u.uid)
                    createForeignNotebook(u.uid, account.accountId)
                    u.sendEmailVerification().await()
                }
                DataState.data(
                    data = AuthenticationResult.SignedUp(user!!),
                    message = "Successfully created. Please verify your email before login."
                )
            } ?: throw ExceptionCatcher.GenericException(
                ErrorCode.ERROR_UNKNOWN_STATE,
                "AuthResult is null for the signed up user."
            )
        } catch (ex: Exception) {
            auth.currentUser?.delete()?.await()
            throw ex
        }
    }

    private suspend fun createForeignNotebook(uid: String, accountId: String) {
        val notebook = RemoteEntityNotebook(
            FirebaseConstants.foreignNotebookKey,
            FirebaseConstants.foreignNotebookName,
            creatorUserId = uid,
            accountId = accountId,
            metadata = mapOf(
                NotebookMetadataConstants.isForeign to true.toString(),
                //a cloud function will import pages in the backend
                NotebookMetadataConstants.importStatus to ImportStatus.IMPORTING.ordinal.toString(),
            )
        )
        FirebaseHelper.getNotebookCollection(accountId)
            .document(FirebaseConstants.foreignNotebookKey).set(notebook).await()
    }

    /**
     * Save user in db under user collection.
     */
    private suspend fun createUserInDb(email: String, accountId: String, uid: String): User {
        val user = User(
            uid,
            AppConstants.EMPTY_STRING, //TODO change this when doing alias setup, currently not set on sign up
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
        val data = mapOf<String, Any>(
            user::loggedInAt.name to System.currentTimeMillis(),
            user::loggedInDeviceId.name to loggedInDeviceId
        )
        FirebaseHelper.getUsersDocReference(user.email).update(data).await()
    }

    /**
     * Clean login device for the user.
     */
    private suspend fun clearLoginDevice(user: User) {
        user.loggedInDeviceId = AppConstants.EMPTY_STRING
        FirebaseHelper.getUsersDocReference(user.email).set(user).await()
    }

}