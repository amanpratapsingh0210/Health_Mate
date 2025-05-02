package com.healthmate.app.data.auth

import com.healthmate.app.data.local.PreferencesManager
import com.healthmate.app.data.local.dao.UserDao
import com.healthmate.app.data.local.entities.toUser
import com.healthmate.app.data.local.entities.toUserEntity
import com.healthmate.app.model.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

class LocalAuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val preferencesManager: PreferencesManager
) : AuthRepository {

    override val currentUser: Flow<User?> = flow {
        preferencesManager.currentUserId.collect { userId ->
            if (userId.isNotEmpty()) {
                val user = userDao.getUserById(userId)?.toUser()
                emit(user)
            } else {
                emit(null)
            }
        }
    }

    override val isUserAuthenticated: Flow<Boolean> = preferencesManager.currentUserId
        .map { it.isNotEmpty() }

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val hashedPassword = hashPassword(password)
            val user = userDao.getUserByEmailAndPassword(email, hashedPassword)
                ?: return Result.failure(Exception("Authentication failed"))
            
            preferencesManager.setCurrentUserId(user.id)
            Result.success(user.toUser())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String, displayName: String): Result<User> {
        return try {
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                return Result.failure(Exception("User with this email already exists"))
            }
            
            val hashedPassword = hashPassword(password)
            val newUser = User(
                id = generateUserId(),
                email = email,
                displayName = displayName,
                createdAt = System.currentTimeMillis()
            )
            
            userDao.insertUser(newUser.toUserEntity(hashedPassword))
            preferencesManager.setCurrentUserId(newUser.id)
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        preferencesManager.clearCurrentUserId()
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            // In a real app, you would implement a proper password reset flow
            // This is a simplified version that just checks if the user exists
            @Suppress("UNUSED_VARIABLE")
            val user = userDao.getUserByEmail(email) 
                ?: return Result.failure(Exception("User not found"))
            
            // In a real implementation, you would generate a token and send an email
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<Unit> {
        return try {
            val userId = preferencesManager.getCurrentUserId()
            val user = userDao.getUserById(userId) ?: return Result.failure(Exception("User not found"))
            
            val updatedUser = user.copy(
                displayName = displayName ?: user.displayName,
                photoUrl = photoUrl ?: user.photoUrl
            )
            
            userDao.updateUser(updatedUser)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    private fun generateUserId(): String {
        return UUID.randomUUID().toString()
    }
} 