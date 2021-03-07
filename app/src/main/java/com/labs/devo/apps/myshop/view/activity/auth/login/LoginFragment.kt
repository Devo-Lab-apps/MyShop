package com.labs.devo.apps.myshop.view.activity.auth.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
//                dataStateHandler.onDataStateChange(DataState.loading<Boolean>(true))
                val emailId = loginEmailAddress.text.toString()
                val pwd = loginPassword.text.toString()
                //Disable to prevent repress
                loginBtn.isEnabled = false
                openSignupActivityBtn.isEnabled = false
                loginProgressBar.visibility = View.VISIBLE
                viewModel.loginUser(LoginUserCredentials(emailId, pwd))
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
                    is LoginViewModel.LoginEvent.ShowInvalidInputMessage -> {
                        binding.loginEmailAddress.clearFocus()
                        binding.loginPassword.clearFocus()
                        if (event.msg != null) {
                            dataStateHandler.onDataStateChange(
                                DataState.message<String>(event.msg)
                            )
                        }
                    }
                    is LoginViewModel.LoginEvent.UserLoggedIn -> {
                        if (event.msg != null) {
                            dataStateHandler.onDataStateChange(
                                DataState.message<String>(event.msg)
                            )
                        }
                        startActivity(Intent(requireContext(), HomeActivity::class.java))
                        requireActivity().finish()
                    }

                    is LoginViewModel.LoginEvent.LogoutOfAllDeviceError -> {
                        if (event.msg != null) {
                            showLogoutDialog(event.msg)
                        } else {
                            showLogoutDialog("Please logout of all devices before login")
                        }
                        dataStateHandler.onDataStateChange(DataState.loading<Nothing>(false))
                    }

                    is LoginViewModel.LoginEvent.LoggedOfAllDevices -> {
                        if (event.msg != null) {
                            dataStateHandler.onDataStateChange(
                                DataState.message<String>(event.msg)
                            )
                        } else {
                            dataStateHandler.onDataStateChange(
                                DataState.message<String>("Logged out of all devices")
                            )
                        }
                    }
                }
                binding.loginBtn.isEnabled = true
                binding.openSignupActivityBtn.isEnabled = true
                binding.loginProgressBar.visibility = View.GONE
            }
        }
    }

    /**
     * Show dialog to logout of all devices
     */
    private fun showLogoutDialog(msg: String) {
        val emailId = binding.loginEmailAddress.text.toString()
        val pwd = binding.loginPassword.text.toString()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(msg)
            .setPositiveButton("Logout") { _, _ ->
                //Disable to prevent repress
                binding.loginBtn.isEnabled = false
                dataStateHandler.onDataStateChange(DataState.loading<Nothing>(true))
                viewModel.logoutOfAllDevices(LoginUserCredentials(emailId, pwd))
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.setCancelable(false)
            .show()
        dataStateHandler.onDataStateChange(DataState.loading<Nothing>(false))
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