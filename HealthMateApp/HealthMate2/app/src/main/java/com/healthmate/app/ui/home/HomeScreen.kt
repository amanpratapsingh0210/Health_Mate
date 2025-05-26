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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Badge
import androidx.compose.material.Icon as MaterialIcon
import androidx.compose.material.IconButton as MaterialIconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface as MaterialSurface
import androidx.compose.material.Text as MaterialText
import androidx.compose.material.TopAppBar
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FabPosition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.StrokeCap
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
import com.healthmate.app.ui.home.profile.ProfileScreen
import com.healthmate.app.ui.theme.CoralLight
import com.healthmate.app.ui.theme.CoralSecondary
import com.healthmate.app.ui.theme.LeafGreen
import com.healthmate.app.ui.theme.MintLight
import com.healthmate.app.ui.theme.MintPrimary
import com.healthmate.app.ui.theme.PurpleLight
import com.healthmate.app.ui.theme.PurpleTertiary
import com.healthmate.app.ui.theme.SkyBlue
import com.healthmate.app.ui.theme.SunnyYellow
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.animation.core.animateFloatAsState

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
        // No topBar - removed as per redesign
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Main container with shadow and rounded corners
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .clip(RoundedCornerShape(35.dp))
                        .background(Color.White)
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(35.dp),
                            spotColor = Color(0xFF000000).copy(alpha = 0.1f)
                        )
                ) {
                    // Nav items container
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Home icon - left side
                        NavBarIcon(
                            icon = Icons.Outlined.Home,
                            iconSelected = Icons.Filled.Home,
                            title = "Home",
                            color = Color(0xFF10B981), // emerald-500
                            selected = currentRoute == homeRoute,
                            onClick = { navController.navigate(homeRoute) { popUpTo(homeRoute) } }
                        )
                        
                        // Nutrition icon
                        NavBarIcon(
                            icon = Icons.Outlined.RestaurantMenu,
                            iconSelected = Icons.Filled.RestaurantMenu,
                            title = "Nutrition",
                            color = Color(0xFFF87171), // rose-400
                            selected = currentRoute == nutritionRoute,
                            onClick = { navController.navigate(nutritionRoute) }
                        )
                        
                        // Empty space for FAB
                        Spacer(modifier = Modifier.width(50.dp))
                        
                        // Workout icon
                        NavBarIcon(
                            icon = Icons.Outlined.FitnessCenter,
                            iconSelected = Icons.Filled.FitnessCenter,
                            title = "Workout",
                            color = Color(0xFFC084FC), // purple-400
                            selected = currentRoute == workoutRoute,
                            onClick = { navController.navigate(workoutRoute) }
                        )
                        
                        // Profile icon - right side
                        NavBarIcon(
                            icon = Icons.Outlined.AccountCircle,
                            iconSelected = Icons.Filled.AccountCircle,
                            title = "Profile",
                            color = Color(0xFF60A5FA), // blue-400
                            selected = currentRoute == profileRoute,
                            onClick = { navController.navigate(profileRoute) }
                        )
                    }
                }
                
                // FAB overlay
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = (-25).dp)
                ) {
                    // Circle background with white border
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981)) // emerald-500
                            .border(
                                width = 4.dp,
                                color = Color.White,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { navController.navigate(scannerRoute) },
                            modifier = Modifier.size(70.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Camera,
                                contentDescription = "Scan Food",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = { /* Empty - we're handling the FAB manually in the bottomBar */ },
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
                ScannerScreen(navController)
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

@Composable
private fun NavBarIcon(
    icon: ImageVector,
    iconSelected: ImageVector,
    title: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    val iconSize = 56.dp
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(CircleShape)
                .background(if (selected) color.copy(alpha = 0.1f) else Color.Transparent)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = false,
                        radius = 28.dp,
                        color = color
                    ),
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            MaterialIcon(
                imageVector = if (selected) iconSelected else icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
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
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFECFDF5), // emerald-50
                        Color.White
                    )
                )
            )
    ) {
        // Scrollable content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Add top padding for status bar
            itemsIndexed(listOf(Unit)) { _, _ ->
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Search Bar
            itemsIndexed(listOf(Unit)) { _, _ ->
                SearchBar(
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Welcome Section
            itemsIndexed(listOf(Unit)) { _, _ ->
                WelcomeCard(
                    userName = currentUser?.displayName ?: "User",
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Quick Access Section
            itemsIndexed(listOf(Unit)) { _, _ ->
                MaterialText(
                    text = "Quick Access",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B), // slate-800
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 12.dp)
                )
                
                QuickAccessGrid(
                    onScanFood = { navController.navigate("scanner") },
                    onNutrition = { navController.navigate("nutrition") },
                    onWorkout = { navController.navigate("workout") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Today's Progress Section
            itemsIndexed(listOf(Unit)) { _, _ ->
                MaterialText(
                    text = "Today's Progress",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B), // slate-800
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 12.dp)
                )
                
                ProgressCard(
                    steps = 5840,
                    stepsGoal = 10000,
                    calories = 1240,
                    caloriesGoal = 2200,
                    water = 4,
                    waterGoal = 8,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Health Insights Section
            itemsIndexed(listOf(Unit)) { _, _ ->
                MaterialText(
                    text = "Health Insights",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B), // slate-800
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 12.dp)
                )
                
                HealthInsightsCard(
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Upcoming Activities Section
            itemsIndexed(listOf(Unit)) { _, _ ->
                MaterialText(
                    text = "Upcoming Activities",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B), // slate-800
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 12.dp)
                )
                
                UpcomingActivitiesList(
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Recommended Workouts Section
            itemsIndexed(listOf(Unit)) { _, _ ->
                MaterialText(
                    text = "Recommended Workouts",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B), // slate-800
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 12.dp)
                )
                
                RecommendedWorkoutsList(
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Add bottom padding for navigation
            itemsIndexed(listOf(Unit)) { _, _ ->
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}

@Composable
private fun SearchBar(modifier: Modifier = Modifier) {
    var searchText by remember { mutableStateOf("") }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFFE2E8F0), // slate-200
                shape = RoundedCornerShape(28.dp)
            )
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = Color(0xFF0F172A).copy(alpha = 0.05f) // slate-900
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MaterialIcon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFF10B981), // emerald-500
                modifier = Modifier.size(20.dp)
            )
            
            BasicTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                textStyle = TextStyle(
                    color = Color(0xFF1E293B), // slate-800
                    fontSize = 16.sp
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box {
                        if (searchText.isEmpty()) {
                            MaterialText(
                                text = "Search for meals, workouts, etc.",
                                color = Color(0xFF94A3B8), // slate-400
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )
            
            if (searchText.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981)) // emerald-500
                        .clickable { searchText = "" },
                    contentAlignment = Alignment.Center
                ) {
                    MaterialIcon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981).copy(alpha = 0.1f)) // emerald-500
                        .border(
                            width = 1.dp,
                            color = Color(0xFF10B981), // emerald-500
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    MaterialIcon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF10B981), // emerald-500
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeCard(
    userName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFF10B981).copy(alpha = 0.3f) // emerald-500
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF10B981), // emerald-500
                            Color(0xFF0D9488)  // teal-600
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .shadow(
                            elevation = 4.dp,
                            shape = CircleShape,
                            spotColor = Color.Black.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    MaterialText(
                        text = userName.take(2).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                ) {
                    MaterialText(
                        text = "Welcome back!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    MaterialText(
                        text = userName,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickAccessGrid(
    onScanFood: () -> Unit,
    onNutrition: () -> Unit,
    onWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Scan Food Button
        QuickAccessButton(
            text = "Scan",
            icon = Icons.Default.Camera,
            color = Color(0xFF10B981), // emerald-500
            onClick = onScanFood,
            modifier = Modifier.weight(1f)
        )
        
        // Nutrition Button
        QuickAccessButton(
            text = "Nutrition",
            icon = Icons.Default.RestaurantMenu,
            color = Color(0xFFF43F5E), // rose-500
            onClick = onNutrition,
            modifier = Modifier.weight(1f)
        )
        
        // Workout Button
        QuickAccessButton(
            text = "Workout",
            icon = Icons.Default.FitnessCenter,
            color = Color(0xFFA855F7), // purple-500
            onClick = onWorkout,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickAccessButton(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(100.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = color.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(32.dp))
            .background(color.copy(alpha = 0.1f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = color,
                modifier = Modifier.size(36.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ProgressCard(
    steps: Int,
    stepsGoal: Int,
    calories: Int,
    caloriesGoal: Int,
    water: Int,
    waterGoal: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFF0F172A).copy(alpha = 0.2f) // slate-900
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B) // slate-900
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProgressItem(
                value = steps,
                goal = stepsGoal,
                label = "Steps",
                color = Color(0xFF10B981) // emerald-400
            )
            
            ProgressItem(
                value = calories,
                goal = caloriesGoal,
                label = "Calories",
                color = Color(0xFFFB7185) // rose-400
            )
            
            ProgressItem(
                value = water,
                goal = waterGoal,
                label = "Water",
                color = Color(0xFF38BDF8) // sky-400
            )
        }
    }
}

@Composable
private fun ProgressItem(
    value: Int,
    goal: Int,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background ring
            CircularProgressIndicator(
                progress = 1f,
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF334155), // slate-700
                strokeWidth = 8.dp
            )
            
            // Progress ring with rounded cap
            CircularProgressIndicator(
                progress = value.toFloat() / goal,
                modifier = Modifier.fillMaxSize(),
                color = color,
                strokeWidth = 8.dp,
                strokeCap = StrokeCap.Round
            )
            
            // Value in center
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MaterialText(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        MaterialText(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
        
        MaterialText(
            text = "Goal: $goal",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF94A3B8) // slate-400
        )
    }
}

@Composable
private fun HealthInsightsCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0xFF0F172A).copy(alpha = 0.05f) // slate-900
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    MaterialIcon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = Color(0xFF10B981), // emerald-500
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                MaterialText(
                    text = "Weekly Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F172A) // slate-900
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9)), // slate-100
                contentAlignment = Alignment.Center
            ) {
                MaterialText(
                    text = "Weekly activity chart",
                    color = Color(0xFF94A3B8) // slate-400
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    MaterialIcon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = Color(0xFF10B981), // emerald-500
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                MaterialText(
                    text = "You're 15% more active than last week. Keep it up!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B) // slate-500
                )
            }
        }
    }
}

@Composable
private fun RecommendedWorkoutsList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WorkoutItem(
            title = "Full Body HIIT",
            duration = "20 min",
            calories = "250 calories",
            icon = Icons.Default.FitnessCenter,
            color = Color(0xFFA855F7) // purple-500
        )
        
        WorkoutItem(
            title = "Cardio Blast",
            duration = "30 min",
            calories = "320 calories",
            icon = Icons.Default.Favorite,
            color = Color(0xFF10B981) // emerald-500
        )
        
        WorkoutItem(
            title = "Yoga Flow",
            duration = "45 min",
            calories = "180 calories",
            icon = Icons.Default.SelfImprovement,
            color = Color(0xFF3B82F6) // blue-500
        )
        
        WorkoutItem(
            title = "Strength Training",
            duration = "40 min",
            calories = "300 calories",
            icon = Icons.Default.FitnessCenter,
            color = Color(0xFFF97316) // orange-500
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutItem(
    title: String,
    duration: String,
    calories: String,
    icon: ImageVector,
    color: Color
) {
    var isPressed by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(
                elevation = if (isPressed) 2.dp else 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = color.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.1f))
            .clickable { 
                /* Handle click */ 
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .scale(if (isPressed) 0.98f else 1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Title and description
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = color.copy(alpha = 0.9f)
                )
                Text(
                    text = "$duration • $calories",
                    style = MaterialTheme.typography.bodyMedium,
                    color = color.copy(alpha = 0.7f)
                )
            }
            
            // Play button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f))
                    .clickable { /* Start workout */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start workout",
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun UpcomingActivitiesList(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0xFF0F172A).copy(alpha = 0.05f) // slate-900
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEFF6FF) // blue-50
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Event icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B82F6).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6), // blue-500
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "Yoga Session",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E3A8A) // blue-900
                    )
                    
                    Text(
                        text = "Tomorrow, 08:00 AM",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF3B82F6) // blue-500
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Notification button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B82F6).copy(alpha = 0.1f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(
                                bounded = true,
                                radius = 18.dp,
                                color = Color(0xFF3B82F6) // blue-500
                            ),
                            onClick = {}
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Set reminder",
                        tint = Color(0xFF3B82F6), // blue-500
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
} 