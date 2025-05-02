package com.healthmate.app.data.auth

import com.healthmate.app.model.user.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    val isUserAuthenticated: Flow<Boolean>
    
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(email: String, password: String, displayName: String): Result<User>
    suspend fun signOut()
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<Unit>
} 