package com.labs.devo.apps.myshop.view.activity.auth.signup

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.auth.SignUpUserCredentials
import com.labs.devo.apps.myshop.databinding.FragmentSignupBinding
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_signup) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName
    private lateinit var dataStateHandler: DataStateListener
    private val viewModel: SignUpViewModel by viewModels()
    private lateinit var binding: FragmentSignupBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignupBinding.bind(view)


        binding.apply {

            signupBtn.setOnClickListener {
                dataStateHandler.onDataStateChange(DataState.loading<Boolean>(true))
                val emailId = signupEmailAddress.text.toString()
                val pwd = signupPassword.text.toString()
                signupBtn.isEnabled = false
                //TODO change confirm pwd
                viewModel.signUpUser(SignUpUserCredentials(emailId, pwd, pwd))
            }

        }
        observeEvents()
    }

    /**
     * Listen view model events.
     */
    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                when (event) {
                    is SignUpViewModel.SignUpEvent.UserSignedUp -> {
                        if (event.msg != null) {
                            dataStateHandler.onDataStateChange(
                                DataState.message<String>(event.msg)
                            )
                        }
                        findNavController().popBackStack()
                    }
                    is SignUpViewModel.SignUpEvent.ShowInvalidInputMessage -> {
                        binding.signupEmailAddress.clearFocus()
                        binding.signupPassword.clearFocus()
                        if (event.msg != null) {
                            dataStateHandler.onDataStateChange(
                                DataState.message<String>(event.msg)
                            )
                        }
                    }
                }
                binding.signupBtn.isEnabled = true
            }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dataStateHandler = context as DataStateListener
        } catch (e: ClassCastException) {
            println("$context must implement DataStateListener")
        }

    }
}