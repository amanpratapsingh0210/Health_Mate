package com.healthmate.app.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.healthmate.app.model.user.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.let { mapFirebaseUser(it) })
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override val isUserAuthenticated: Flow<Boolean> = currentUser.map { it != null }

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { 
                Result.success(mapFirebaseUser(it))
            } ?: Result.failure(Exception("Authentication failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String, displayName: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                // Update display name
                firebaseUser.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()
                ).await()
                
                // Create user document in Firestore
                val user = User(
                    id = firebaseUser.uid,
                    email = email,
                    displayName = displayName,
                    createdAt = System.currentTimeMillis()
                )
                
                firestore.collection("users")
                    .document(user.id)
                    .set(user)
                    .await()
                
                Result.success(user)
            } ?: Result.failure(Exception("User creation failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<Unit> {
        return try {
            val currentUser = auth.currentUser ?: return Result.failure(Exception("No user logged in"))
            
            val profileUpdates = UserProfileChangeRequest.Builder().apply {
                displayName?.let { setDisplayName(it) }
                photoUrl?.let { setPhotoUri(android.net.Uri.parse(it)) }
            }.build()
            
            currentUser.updateProfile(profileUpdates).await()
            
            // Update Firestore user document
            firestore.collection("users")
                .document(currentUser.uid)
                .update(
                    mapOf(
                        "displayName" to (displayName ?: currentUser.displayName),
                        "photoUrl" to (photoUrl ?: currentUser.photoUrl?.toString())
                    )
                )
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun mapFirebaseUser(firebaseUser: com.google.firebase.auth.FirebaseUser): User {
        return User(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            displayName = firebaseUser.displayName ?: "",
            photoUrl = firebaseUser.photoUrl?.toString() ?: ""
        )
    }
} 