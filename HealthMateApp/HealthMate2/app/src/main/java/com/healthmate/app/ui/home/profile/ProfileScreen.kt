package com.healthmate.app.ui.home.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val updateProfileState by viewModel.updateProfileState.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.setSelectedImageUri(it)
        }
    }

    // Monitor update profile state
    LaunchedEffect(updateProfileState) {
        when (updateProfileState) {
            is UpdateProfileState.Success -> {
                snackbarHostState.showSnackbar("Profile updated successfully!")
                viewModel.resetUpdateState()
            }
            is UpdateProfileState.Error -> {
                snackbarHostState.showSnackbar("Error: ${(updateProfileState as UpdateProfileState.Error).message}")
                viewModel.resetUpdateState()
            }
            else -> { /* do nothing */ }
        }
    }
    
    if (showEditProfileDialog) {
        EditProfileDialog(
            name = editedName,
            currentImageUri = selectedImageUri ?: Uri.parse(currentUser?.photoUrl ?: ""),
            onNameChange = { editedName = it },
            onImagePick = { imagePickerLauncher.launch("image/*") },
            onConfirm = {
                viewModel.updateProfileWithImage(editedName, selectedImageUri)
                showEditProfileDialog = false
            },
            onDismiss = { 
                showEditProfileDialog = false
                viewModel.setSelectedImageUri(null)
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile", 
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
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
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Profile Info Section
                item {
                    ProfileHeader(
                        name = currentUser?.displayName ?: "User",
                        email = currentUser?.email ?: "user@example.com",
                        photoUrl = currentUser?.photoUrl,
                        onEditClick = {
                            editedName = currentUser?.displayName ?: ""
                            // Reset selected image
                            viewModel.setSelectedImageUri(null)
                            showEditProfileDialog = true
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                // Account Section
                item {
                    SectionHeader(title = "Account")
                    
                    ProfileMenuCard {
                        ProfileMenuItem(
                            icon = Icons.Default.Person,
                            title = "Personal Information",
                            subtitle = "Manage your personal details",
                            iconTint = Color(0xFF10B981) // emerald-500
                        )
                        
                        Divider(
                            modifier = Modifier.padding(start = 56.dp),
                            color = Color(0xFFE2E8F0) // slate-200
                        )
                        
                        ProfileMenuItem(
                            icon = Icons.Default.Notifications,
                            title = "Notifications",
                            subtitle = "Manage your notification preferences",
                            iconTint = Color(0xFFF87171) // rose-400
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                // App Settings Section
                item {
                    SectionHeader(title = "App Settings")
                    
                    ProfileMenuCard {
                        ProfileMenuItem(
                            icon = Icons.Default.Settings,
                            title = "Preferences",
                            subtitle = "Units, language, etc.",
                            iconTint = Color(0xFF60A5FA) // blue-400
                        )
                        
                        Divider(
                            modifier = Modifier.padding(start = 56.dp),
                            color = Color(0xFFE2E8F0) // slate-200
                        )
                        
                        ProfileMenuItem(
                            icon = Icons.Default.Brush,
                            title = "Appearance",
                            subtitle = "Theme, colors, and display options",
                            iconTint = Color(0xFFA855F7) // purple-500
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                // About & Support Section
                item {
                    SectionHeader(title = "About & Support")
                    
                    ProfileMenuCard {
                        ProfileMenuItem(
                            icon = Icons.Outlined.Info,
                            title = "About HealthMate",
                            subtitle = "Version 1.0.0",
                            iconTint = Color(0xFF6D28D9) // violet-700
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(36.dp))
                }
                
                // Sign Out Button
                item {
                    Button(
                        onClick = {
                            viewModel.signOut()
                            onSignOut()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(4.dp, RoundedCornerShape(28.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444) // red-500
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sign Out",
                            tint = Color.White
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Sign Out",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
            
            // Show loading indicator if updating profile
            if (updateProfileState is UpdateProfileState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF10B981) // emerald-500
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    email: String,
    photoUrl: String?,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFF0F172A).copy(alpha = 0.05f) // slate-900
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile picture with edit button side by side
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Profile picture
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981).copy(alpha = 0.1f)) // emerald-500
                        .border(
                            width = 4.dp,
                            color = Color(0xFF10B981).copy(alpha = 0.2f), // emerald-500
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUrl.isNullOrEmpty()) {
                        // Default icon if no photo
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile Picture",
                            tint = Color(0xFF10B981), // emerald-500
                            modifier = Modifier.size(64.dp)
                        )
                    } else {
                        // Load actual photo
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(photoUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Edit button as separate element
                Button(
                    onClick = onEditClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981) // emerald-500
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Edit Profile",
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A) // slate-900
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64748B) // slate-500
            )
        }
    }
}

@Composable
fun ProfileImage(
    photoUrl: String?,
    size: Dp = 40.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFF10B981).copy(alpha = 0.1f)) // emerald-500
            .border(
                width = 2.dp,
                color = Color(0xFF10B981).copy(alpha = 0.2f), // emerald-500
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (photoUrl.isNullOrEmpty()) {
            // Default icon if no photo
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Picture",
                tint = Color(0xFF10B981), // emerald-500
                modifier = Modifier.size(size * 0.6f)
            )
        } else {
            // Load actual photo
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF0F172A), // slate-900
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun ProfileMenuCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0xFF0F172A).copy(alpha = 0.05f) // slate-900
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A) // slate-900
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B) // slate-500
            )
        }
        
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Go to $title",
            tint = Color(0xFFCBD5E1), // slate-300
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun EditProfileDialog(
    name: String,
    currentImageUri: Uri,
    onNameChange: (String) -> Unit,
    onImagePick: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Profile",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A) // slate-900
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Update your profile information",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B) // slate-500
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Profile image preview and picker
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981).copy(alpha = 0.1f))
                        .border(
                            width = 2.dp,
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                        .clickable(onClick = onImagePick),
                    contentAlignment = Alignment.Center
                ) {
                    if (currentImageUri.toString().isNotEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(currentImageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile Picture",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(60.dp)
                        )
                    }
                    
                    // Camera icon overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change Photo",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 