package com.healthmate.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthmate.app.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val isAuthenticated: StateFlow<Boolean> = authRepository.isUserAuthenticated
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun signIn(email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.signIn(email, password)
            onResult(result.map { })
        }
    }

    fun signUp(email: String, password: String, displayName: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.signUp(email, password, displayName)
            onResult(result.map { })
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun resetPassword(email: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.resetPassword(email)
            onResult(result)
        }
    }
} 