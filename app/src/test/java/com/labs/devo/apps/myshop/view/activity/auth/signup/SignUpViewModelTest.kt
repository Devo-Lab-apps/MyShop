package com.labs.devo.apps.myshop.view.activity.auth.signup

import com.google.common.truth.Truth.assertThat
import com.labs.devo.apps.myshop.business.auth.FakeUserAuth
import com.labs.devo.apps.myshop.business.auth.abstraction.UserAuth
import com.labs.devo.apps.myshop.business.helper.UserManager
import com.labs.devo.apps.myshop.const.ErrorMessages.UNKNOWN_ERROR_OCCURRED_RETRY
import com.labs.devo.apps.myshop.data.models.auth.SignUpUserCredentials
import com.labs.devo.apps.myshop.util.MainCoroutineRule
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class SignUpViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var signUpViewModel: SignUpViewModel

    lateinit var userAuth: FakeUserAuth

    @Before
    fun setup() {
        userAuth = FakeUserAuth()
        signUpViewModel = SignUpViewModel(userAuth)
        userAuth.shouldReturnAuthError = false
        userAuth.shouldReturnDatabaseError = false
    }

    @After
    fun teardown(){
        UserManager.initUser(null)
    }

    @Test
    fun signUpEmailInvalidTest_invalidEmailError() = runBlocking {
        val channel = signUpViewModel.channelFlow
        signUpViewModel.signUpUser(SignUpUserCredentials("abc@gmail", "123456", "123456"))
        val res = channel.first()
        assertThat(res).isEqualTo(SignUpViewModel.SignUpEvent.ShowInvalidInputMessage("Invalid email entered"))
        assertNull(UserManager.user)
    }

    @Test
    fun signUpShortPasswordTest_invalidPasswordError() = runBlocking {
        val channel = signUpViewModel.channelFlow
        signUpViewModel.signUpUser(SignUpUserCredentials("abc@gmail.com", "12345", "12345"))
        val res = channel.first()
        assertThat(res).isEqualTo(SignUpViewModel.SignUpEvent.ShowInvalidInputMessage("Password must be of length greater than 6"))
        assertNull(UserManager.user)
    }

    @Test
    fun signUpPasswordNotMatchingTest_passwordDontMatchError() = runBlocking {
        val channel = signUpViewModel.channelFlow
        signUpViewModel.signUpUser(SignUpUserCredentials("abc@gmail.com", "123456", "1234512"))
        val res = channel.first()
        assertThat(res).isEqualTo(SignUpViewModel.SignUpEvent.ShowInvalidInputMessage("Passwords don't match"))
        assertNull(UserManager.user)
    }

    @Test
    fun signUpTest_signsUp() = runBlocking {
        val channel = signUpViewModel.channelFlow
        signUpViewModel.signUpUser(SignUpUserCredentials("abc@gmail.com", "123456", "123456"))
        val res = channel.first()
        assertThat(res).isEqualTo(SignUpViewModel.SignUpEvent.UserSignedUp("User signed up"))
        assertNotNull(UserManager.user)
    }

    @Test
    fun signUpNoDBConnectionTest_dbError() = runBlocking {
        userAuth.shouldReturnDatabaseError = true
        val channel = signUpViewModel.channelFlow
        signUpViewModel.signUpUser(SignUpUserCredentials("abc@gmail.com", "123456", "123456"))
        val res = channel.first()
        assertThat(res).isEqualTo(SignUpViewModel.SignUpEvent.ShowInvalidInputMessage(
            "Unable to connect to DB"
        ))
        assertNull(UserManager.user)
    }

    @Test
    fun signUpNoAuthConnectionTest_authError() = runBlocking {
        userAuth.shouldReturnAuthError = true
        val channel = signUpViewModel.channelFlow
        signUpViewModel.signUpUser(SignUpUserCredentials("abc@gmail.com", "123456", "123456"))
        val res = channel.first()
        assertThat(res).isEqualTo(SignUpViewModel.SignUpEvent.ShowInvalidInputMessage(
            UNKNOWN_ERROR_OCCURRED_RETRY
        ))
        assertNull(UserManager.user)
    }


}