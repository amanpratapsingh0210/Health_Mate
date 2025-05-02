package com.healthmate.app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Badge
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.healthmate.app.ui.home.nutrition.NutritionTrackerScreen
import com.healthmate.app.ui.home.scanner.ScannerScreen
import com.healthmate.app.ui.home.workout.WorkoutScreen
import com.healthmate.app.ui.theme.CoralLight
import com.healthmate.app.ui.theme.CoralSecondary
import com.healthmate.app.ui.theme.LeafGreen
import com.healthmate.app.ui.theme.MintLight
import com.healthmate.app.ui.theme.MintPrimary
import com.healthmate.app.ui.theme.PurpleLight
import com.healthmate.app.ui.theme.PurpleTertiary
import com.healthmate.app.ui.theme.SkyBlue
import com.healthmate.app.ui.theme.SunnyYellow

@Composable
fun HomeScreen(
    onSignOut: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Define routes
    val homeRoute = "home"
    val nutritionRoute = "nutrition"
    val scannerRoute = "scanner"
    val workoutRoute = "workout"
    val profileRoute = "profile"
    
    // Create NavController
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: homeRoute
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = when (currentRoute) {
                            homeRoute -> "HealthMate"
                            nutritionRoute -> "Nutrition Tracker"
                            scannerRoute -> "Food Scanner"
                            workoutRoute -> "Workout"
                            profileRoute -> "Profile"
                            else -> "HealthMate"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ) 
                },
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sign Out",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                val items = listOf(
                    BottomNavItemData("Home", Icons.Filled.Home, Icons.Outlined.Home, homeRoute),
                    BottomNavItemData("Nutrition", Icons.Filled.RestaurantMenu, Icons.Outlined.RestaurantMenu, nutritionRoute),
                    BottomNavItemData("Workout", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter, workoutRoute),
                    BottomNavItemData("Profile", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle, profileRoute)
                )
                
                items.forEach { item ->
                    BottomNavigationItem(
                        selected = currentRoute == item.route,
                        onClick = { 
                            if (item.route == homeRoute) {
                                navController.navigate(item.route) { popUpTo(homeRoute) }
                            } else {
                                navController.navigate(item.route)
                            }
                        },
                        icon = {
                            if (item.hasNotification) {
                                Box {
                                    Icon(
                                        imageVector = if (currentRoute == item.route) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                    Badge { }
                                }
                            } else {
                                Icon(
                                    imageVector = if (currentRoute == item.route) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            }
                        },
                        label = { Text(text = item.title) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == homeRoute) {
                FloatingActionButton(
                    onClick = { navController.navigate(scannerRoute) },
                    backgroundColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Scan Food"
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = homeRoute,
            modifier = Modifier.padding(padding)
        ) {
            composable(homeRoute) {
                DashboardScreen(navController, viewModel)
            }
            composable(nutritionRoute) {
                NutritionTrackerScreen()
            }
            composable(scannerRoute) {
                ScannerScreen()
            }
            composable(workoutRoute) {
                WorkoutScreen()
            }
            composable(profileRoute) {
                ProfileScreen(onSignOut)
            }
        }
    }
}

data class BottomNavItemData(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String,
    val hasNotification: Boolean = false
)

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // User's summary data
        UserSummaryCard(userName = currentUser?.displayName ?: "User")
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Main features section
        Text(
            text = "Quick Access",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 16.dp)
        )
        
        // 3 main feature cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FeatureCard(
                title = "Scan Food",
                icon = Icons.Default.Camera,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("scanner") }
            )
            Spacer(modifier = Modifier.size(8.dp))
            FeatureCard(
                title = "Nutrition",
                icon = Icons.Default.RestaurantMenu,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("nutrition") }
            )
            Spacer(modifier = Modifier.size(8.dp))
            FeatureCard(
                title = "Workout",
                icon = Icons.Default.FitnessCenter,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("workout") }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Daily progress
        Text(
            text = "Today's Progress",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 16.dp)
        )
        
        DailyProgressCard()
    }
}

@Composable
fun UserSummaryCard(userName: String) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile picture
                Box(
                    modifier = Modifier
                        .size(65.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .border(3.dp, Color.White, CircleShape)
                        .padding(4.dp)
                        .shadow(4.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile Picture",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(45.dp)
                    )
                }
                
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = "Welcome back!",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureCard(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cardColor = when(title) {
        "Scan Food" -> MintLight
        "Nutrition" -> CoralLight
        "Workout" -> PurpleLight
        else -> MaterialTheme.colorScheme.primaryContainer
    }
    
    val iconColor = when(title) {
        "Scan Food" -> MintPrimary
        "Nutrition" -> CoralSecondary
        "Workout" -> PurpleTertiary
        else -> color
    }
    
    Card(
        modifier = modifier
            .height(120.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = iconColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DailyProgressCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressItem("Steps", "5,840", "10,000", 0.58f, LeafGreen)
                ProgressItem("Calories", "1,240", "2,200", 0.56f, CoralSecondary)
                ProgressItem("Water", "4", "8 glasses", 0.5f, SkyBlue)
            }
        }
    }
}

@Composable
fun ProgressItem(
    title: String,
    current: String,
    goal: String,
    progress: Float,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Box(
            modifier = Modifier
                .size(65.dp)
                .padding(8.dp)
                .clip(CircleShape)
                .border(2.dp, color.copy(alpha = 0.5f), CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f))
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(color.copy(alpha = progress * 0.7f))
            )
            Text(
                text = current,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = "Goal: $goal",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ProfileScreen(onSignOut: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Profile Screen Coming Soon",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onSignOut,
            modifier = Modifier.shadow(4.dp, RoundedCornerShape(8.dp))
        ) {
            Text("Sign Out")
        }
    }
} 