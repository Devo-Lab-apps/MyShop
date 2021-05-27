package com.labs.devo.apps.myshop.view.activity.auth.signup

import com.google.common.truth.Truth.assertThat
import com.labs.devo.apps.myshop.business.auth.FakeUserAuth
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.const.ErrorMessages.UNKNOWN_ERROR_OCCURRED_RETRY
import com.labs.devo.apps.myshop.data.models.auth.LoginUserCredentials
import com.labs.devo.apps.myshop.util.MainCoroutineRule
import com.labs.devo.apps.myshop.view.activity.auth.login.LoginViewModel
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class LoginViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var loginViewModel: LoginViewModel

    lateinit var userAuth: FakeUserAuth

    @Before
    fun setup() {
        userAuth = FakeUserAuth()
        loginViewModel = LoginViewModel(userAuth)
        userAuth.shouldReturnAuthError = false
        userAuth.shouldReturnDatabaseError = false
    }

    @After
    fun teardown() {
        UserManager.initUser(null)
    }

    @Test
    fun loginEmailInvalidTest_invalidEmailError() = runBlocking {
        val channel = loginViewModel.channelFlow
        loginViewModel.loginUser(LoginUserCredentials("abc@gmail", "123456"))
        val res = channel.first()
        assertThat(res).isEqualTo(LoginViewModel.LoginEvent.ShowInvalidInputMessage("Invalid email entered"))
        assertNull(UserManager.user)
    }

    @Test
    fun loginShortPasswordTest_invalidPasswordError() = runBlocking {
        val channel = loginViewModel.channelFlow
        loginViewModel.loginUser(LoginUserCredentials("abc@gmail.com", "12345"))
        val res = channel.first()
        assertThat(res).isEqualTo(LoginViewModel.LoginEvent.ShowInvalidInputMessage("Password must be of length greater than 6"))
        assertNull(UserManager.user)
    }

    @Test
    fun loginTest_logsIn() = runBlocking {
        val channel = loginViewModel.channelFlow
        loginViewModel.loginUser(LoginUserCredentials("abc@gmail.com", "123456"))
        val res = channel.first()
        assertThat(res).isEqualTo(LoginViewModel.LoginEvent.UserLoggedIn("User logged in"))
        assertNotNull(UserManager.user)
    }

    @Test
    fun loginNoDBConnectionTest_dbError() = runBlocking {
        userAuth.shouldReturnDatabaseError = true
        val channel = loginViewModel.channelFlow
        loginViewModel.loginUser(LoginUserCredentials("abc@gmail.com", "123456"))
        val res = channel.first()
        assertThat(res).isEqualTo(
            LoginViewModel.LoginEvent.ShowInvalidInputMessage(
                "Unable to connect to DB"
            )
        )
        assertNull(UserManager.user)
    }

    @Test
    fun loginNoAuthConnectionTest_authError() = runBlocking {
        userAuth.shouldReturnAuthError = true
        val channel = loginViewModel.channelFlow
        loginViewModel.loginUser(LoginUserCredentials("abc@gmail.com", "123456"))
        val res = channel.first()
        assertThat(res).isEqualTo(
            LoginViewModel.LoginEvent.ShowInvalidInputMessage(
                UNKNOWN_ERROR_OCCURRED_RETRY
            )
        )
        assertNull(UserManager.user)
    }


}