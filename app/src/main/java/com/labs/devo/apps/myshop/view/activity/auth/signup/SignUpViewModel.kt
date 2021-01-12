package com.labs.devo.apps.myshop.view.activity.auth.signup

import com.labs.devo.apps.myshop.view.util.BaseViewModel

class SignUpViewModel: BaseViewModel<SignUpViewModel.SignUpEvent>() {


    sealed class SignUpEvent {
        data class UserSignedUp(val msg: String) : SignUpEvent()
    }
}