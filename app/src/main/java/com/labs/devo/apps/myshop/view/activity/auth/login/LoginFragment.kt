package com.labs.devo.apps.myshop.view.activity.auth.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.auth.LoginUserCredentials
import com.labs.devo.apps.myshop.databinding.FragmentLoginBinding
import com.labs.devo.apps.myshop.view.activity.home.HomeActivity
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName
    private lateinit var dataStateHandler: DataStateListener
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)


        binding.apply {
            openSignupActivityBtn.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            }

            loginBtn.setOnClickListener {
                dataStateHandler.onDataStateChange(DataState.loading<Boolean>(true))
                val emailId = loginEmailAddress.text.toString()
                val pwd = loginPassword.text.toString()
                //Disable to prevent repress
                loginBtn.isEnabled = false

                viewModel.loginUser(LoginUserCredentials(emailId, pwd))
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.channelFlow.collect { event ->
                when (event) {
                    is LoginViewModel.LoginEvent.ShowInvalidInputMessage -> {
                        binding.loginEmailAddress.clearFocus()
                        binding.loginPassword.clearFocus()
                        dataStateHandler.onDataStateChange(
                            DataState.message<String>(event.msg)
                        )
                    }
                    is LoginViewModel.LoginEvent.UserLoggedIn -> {
                        dataStateHandler.onDataStateChange(
                            DataState.message<String>(event.msg)
                        )
                        startActivity(Intent(requireContext(), HomeActivity::class.java))
                        requireActivity().finish()
                    }
                }
                binding.loginBtn.isEnabled = true
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