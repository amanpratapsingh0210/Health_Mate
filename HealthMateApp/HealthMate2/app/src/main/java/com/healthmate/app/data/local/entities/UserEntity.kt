package com.healthmate.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.healthmate.app.model.user.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val password: String,
    val displayName: String,
    val photoUrl: String,
    val createdAt: Long
)

fun UserEntity.toUser(): User {
    return User(
        id = id,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl,
        createdAt = createdAt
    )
}

fun User.toUserEntity(password: String = ""): UserEntity {
    return UserEntity(
        id = id,
        email = email,
        password = password,
        displayName = displayName,
        photoUrl = photoUrl,
        createdAt = createdAt
    )
} 