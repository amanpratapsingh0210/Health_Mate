package com.healthmate.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.healthmate.app.ui.auth.AuthViewModel
import com.healthmate.app.ui.auth.LoginScreen
import com.healthmate.app.ui.auth.SignUpScreen
import com.healthmate.app.ui.home.HomeScreen
import com.healthmate.app.ui.theme.HealthMateTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthMateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
                    
                    NavHost(
                        navController = navController,
                        startDestination = if (isAuthenticated) "home" else "login"
                    ) {
                        composable("login") {
                            LoginScreen(
                                onNavigateToSignUp = { navController.navigate("signup") },
                                onLoginSuccess = { navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }}
                            )
                        }
                        
                        composable("signup") {
                            SignUpScreen(
                                onNavigateToLogin = { navController.navigate("login") },
                                onSignUpSuccess = { navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }}
                            )
                        }
                        
                        composable("home") {
                            HomeScreen(
                                onSignOut = {
                                    authViewModel.signOut()
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}