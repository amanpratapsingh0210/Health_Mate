package com.healthmate.app.di

import com.healthmate.app.data.auth.AuthRepository
import com.healthmate.app.data.auth.LocalAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        localAuthRepository: LocalAuthRepository
    ): AuthRepository
} 