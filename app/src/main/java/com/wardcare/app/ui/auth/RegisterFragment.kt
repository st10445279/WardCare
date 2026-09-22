package com.wardcare.app.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wardcare.app.R
import com.wardcare.app.data.local.UserManager
import com.wardcare.app.databinding.FragmentRegisterBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    private val wards = arrayOf("General Ward", "ICU", "Pediatrics", "Maternity", "Emergency")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWardDropdown()

        binding.registerButton.setOnClickListener {
            if (validateInputs()) {
                val name = binding.nameInput.text.toString().trim()
                val email = binding.emailInput.text.toString().trim()
                val ward = binding.wardDropdown.text.toString().trim()
                val userManager = UserManager(requireContext())
                userManager.userName = name
                userManager.userEmail = email
                if (ward.isNotEmpty()) userManager.userWard = ward

                viewModel.register(
                    name,
                    email,
                    ward,
                    binding.passwordInput.text.toString()
                )
            }
        }

        binding.goToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        observeViewModel()
    }

    private fun setupWardDropdown() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, wards)
        binding.wardDropdown.setAdapter(adapter)
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        val name = binding.nameInput.text.toString()
        if (name.length < 2 || name.length > 60) {
            binding.nameLayout.error = "Name must be 2-60 characters"
            isValid = false
        } else {
            binding.nameLayout.error = null
        }

        val email = binding.emailInput.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.error = "Invalid email format"
            isValid = false
        } else {
            binding.emailLayout.error = null
        }

        val password = binding.passwordInput.text.toString()
        val passwordRegex = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,}$".toRegex()
        if (!password.matches(passwordRegex)) {
            binding.passwordLayout.error = getString(R.string.error_password_requirement)
            isValid = false
        } else {
            binding.passwordLayout.error = null
        }

        val confirmPassword = binding.confirmPasswordInput.text.toString()
        if (confirmPassword != password) {
            binding.confirmPasswordLayout.error = getString(R.string.error_passwords_dont_match)
            isValid = false
        } else {
            binding.confirmPasswordLayout.error = null
        }

        return isValid
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.collectLatest { state ->
                when (state) {
                    is AuthViewModel.AuthState.Loading -> {
                        binding.registerButton.isEnabled = false
                    }
                    is AuthViewModel.AuthState.Success -> {
                        binding.registerButton.isEnabled = true
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }
                    is AuthViewModel.AuthState.Error -> {
                        binding.registerButton.isEnabled = true
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
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
