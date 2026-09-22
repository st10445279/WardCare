package com.wardcare.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wardcare.app.security.PasswordHasher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun register(name: String, email: String, ward: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            // Hash password before sending to API
            val hashedPassword = PasswordHasher.hashPassword(password)
            
            // Mock API Call
            // val response = repository.register(name, email, ward, hashedPassword)
            
            // For now, simulate success
            _authState.value = AuthState.Success("Account created successfully")
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            // In a real app, we send plaintext or hash to API depending on backend design.
            // Requirement says hashed before reaching DB.
            
            // Simulate API call
            _authState.value = AuthState.Success("Logged in successfully")
        }
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val message: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
