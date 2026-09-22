package com.wardcare.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wardcare.app.R
import com.wardcare.app.data.local.UserManager
import com.wardcare.app.databinding.FragmentLoginBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.googleSsoButton.setOnClickListener {
            val userManager = UserManager(requireContext())
            userManager.isLoggedIn = true
            Toast.makeText(requireContext(), "Signed in with Google", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_loginFragment_to_homeDashboardFragment)
        }

        binding.goToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.forgotPassword.setOnClickListener {
            Toast.makeText(requireContext(), "Password reset instructions sent to your email", Toast.LENGTH_LONG).show()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.collectLatest { state ->
                when (state) {
                    is AuthViewModel.AuthState.Loading -> {
                        binding.loginButton.isEnabled = false
                    }
                    is AuthViewModel.AuthState.Success -> {
                        binding.loginButton.isEnabled = true
                        val userManager = UserManager(requireContext())
                        userManager.isLoggedIn = true
                        val email = binding.emailInput.text.toString().trim()
                        if (email.isNotEmpty()) {
                            userManager.userEmail = email
                        }
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_loginFragment_to_homeDashboardFragment)
                    }
                    is AuthViewModel.AuthState.Error -> {
                        binding.loginButton.isEnabled = true
                        Toast.makeText(requireContext(), "Email or password is incorrect", Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
