package com.healthmate.app.ui.home.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthmate.app.data.auth.AuthRepository
import com.healthmate.app.model.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    val currentUser: StateFlow<User?> = authRepository.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    
    private val _updateProfileState = MutableStateFlow<UpdateProfileState>(UpdateProfileState.Idle)
    val updateProfileState = _updateProfileState.asStateFlow()
    
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()
    
    fun updateProfile(displayName: String, photoUrl: String?) {
        viewModelScope.launch {
            _updateProfileState.value = UpdateProfileState.Loading
            
            val result = authRepository.updateProfile(displayName, photoUrl)
            
            _updateProfileState.value = if (result.isSuccess) {
                UpdateProfileState.Success
            } else {
                UpdateProfileState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
    
    fun updateProfileWithImage(displayName: String, imageUri: Uri?) {
        viewModelScope.launch {
            _updateProfileState.value = UpdateProfileState.Loading
            
            // Convert Uri to string URL
            val photoUrl = imageUri?.toString()
            
            val result = authRepository.updateProfile(displayName, photoUrl)
            
            _updateProfileState.value = if (result.isSuccess) {
                UpdateProfileState.Success
            } else {
                UpdateProfileState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
    
    fun setSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
    }
    
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
    
    fun resetUpdateState() {
        _updateProfileState.value = UpdateProfileState.Idle
    }
}

sealed class UpdateProfileState {
    object Idle : UpdateProfileState()
    object Loading : UpdateProfileState()
    object Success : UpdateProfileState()
    data class Error(val message: String) : UpdateProfileState()
} 