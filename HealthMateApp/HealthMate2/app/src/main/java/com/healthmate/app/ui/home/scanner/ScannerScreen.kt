package com.healthmate.app.ui.home.scanner

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ScannerScreen() {
    var scanState by remember { mutableStateOf<ScanState>(ScanState.Initial) }
    var showResultDialog by remember { mutableStateOf(false) }
    var showCameraView by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val context = LocalContext.current
    
    // Permission launchers and gallery launcher - order matters!
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showCameraView = true
        } else {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Define galleryLauncher first since it's used in galleryPermissionLauncher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            capturedImageUri = it
            scanState = ScanState.Scanning
            // Simulate scan completion after brief delay
            simulateScanCompletion { resultState ->
                scanState = resultState
                showResultDialog = true
            }
        }
    }
    
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Launch gallery picker
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Storage permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            showCameraView -> {
                // Simple camera view placeholder until we fix CameraX integration
                CameraViewPlaceholder(
                    onImageCaptured = { 
                        showCameraView = false
                        // Just simulate a capture for now
                        scanState = ScanState.Scanning
                        simulateScanCompletion { resultState ->
                            scanState = resultState
                            showResultDialog = true
                        }
                    },
                    onClose = {
                        showCameraView = false
                    }
                )
            }
            else -> {
                when (scanState) {
                    is ScanState.Initial -> {
                        InitialScannerView(
                            onTakePhoto = {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                            onChooseFromGallery = {
                                val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                    Manifest.permission.READ_MEDIA_IMAGES
                                } else {
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                }
                                galleryPermissionLauncher.launch(permission)
                            }
                        )
                    }
                    is ScanState.Scanning -> {
                        ScanningView(
                            onScanComplete = {
                                // After a brief delay, we'd show the results
                                scanState = ScanState.Result(
                                    foodName = "Grilled Chicken Salad",
                                    calories = 320,
                                    carbs = 12.5,
                                    protein = 38.0,
                                    fat = 14.2
                                )
                                showResultDialog = true
                            }
                        )
                    }
                    is ScanState.Result -> {
                        ResultView(scanState as ScanState.Result) {
                            scanState = ScanState.Initial
                        }
                        
                        if (showResultDialog) {
                            FoodDetailsDialog(
                                foodData = scanState as ScanState.Result,
                                onDismiss = { showResultDialog = false },
                                onConfirm = { 
                                    showResultDialog = false
                                    // Here you would add this to the user's food log
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InitialScannerView(
    onTakePhoto: () -> Unit,
    onChooseFromGallery: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
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
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Camera",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Food Scanner",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Scan food to get nutritional information instantly",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = onTakePhoto,
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = "Take Photo"
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Take Photo")
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = onChooseFromGallery,
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Choose from Gallery"
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Choose from Gallery")
                    }
                }
            }
        }
    }
}

@Composable
fun CameraViewPlaceholder(
    onImageCaptured: () -> Unit,
    onClose: () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        
        // Camera controls overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Close button
                FloatingActionButton(
                    onClick = onClose,
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Camera"
                    )
                }
                
                // Capture button
                FloatingActionButton(
                    onClick = onImageCaptured,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Take Photo",
                        modifier = Modifier.size(36.dp)
                    )
                }
                
                // Placeholder for symmetry
                Box(modifier = Modifier.size(56.dp))
            }
        }
        
        // Camera instructions overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 150.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Point camera at food to capture",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun simulateScanCompletion(onComplete: (ScanState.Result) -> Unit) {
    // In a real app, this would be a call to an ML model or API
    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
        onComplete(
            ScanState.Result(
                foodName = "Grilled Chicken Salad",
                calories = 320,
                carbs = 12.5,
                protein = 38.0,
                fat = 14.2
            )
        )
    }, 2000) // 2 second delay to simulate processing
}

@Composable
fun ScanningView(
    onScanComplete: () -> Unit
) {
    // In a real app, this would analyze the image with ML
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(16.dp)
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Fallback to black background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f))
                )
                
                // Scanning animation overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(80.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Analyzing your food...",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LinearProgressIndicator(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(0.7f),
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        // Simulate scan completion
                        LaunchedEffect(Unit) {
                            kotlinx.coroutines.delay(2000)
                            onScanComplete()
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Position food in the center of the frame",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Our AI will analyze and provide nutritional details",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun ResultView(result: ScanState.Result, onNewScan: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Scan Result",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Food summary
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = result.foodName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "${result.calories} calories",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Nutritional breakdown
        NutrientInfoRow("Carbs", "${result.carbs}g", MaterialTheme.colorScheme.primary)
        NutrientInfoRow("Protein", "${result.protein}g", MaterialTheme.colorScheme.secondary)
        NutrientInfoRow("Fat", "${result.fat}g", MaterialTheme.colorScheme.tertiary)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onNewScan,
                modifier = Modifier.weight(1f)
            ) {
                Text("Scan Again")
            }
            
            Spacer(modifier = Modifier.size(16.dp))
            
            Button(
                onClick = { /* Add to log */ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add to diary"
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text("Add to Diary")
            }
        }
    }
}

@Composable
fun NutrientInfoRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            
            Spacer(modifier = Modifier.size(8.dp))
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailsDialog(
    foodData: ScanState.Result,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var quantity by remember { mutableStateOf("1") }
    var mealType by remember { mutableStateOf("Lunch") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add to Food Diary",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = foodData.foodName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${foodData.calories} calories per serving",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity (servings)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Meal Type",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // In a real app, this would be a dropdown or radio buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MealTypeChip(
                        label = "Breakfast",
                        selected = mealType == "Breakfast",
                        onClick = { mealType = "Breakfast" }
                    )
                    MealTypeChip(
                        label = "Lunch",
                        selected = mealType == "Lunch",
                        onClick = { mealType = "Lunch" }
                    )
                    MealTypeChip(
                        label = "Dinner",
                        selected = mealType == "Dinner",
                        onClick = { mealType = "Dinner" }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Add to Diary")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealTypeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        onClick = onClick
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

sealed class ScanState {
    object Initial : ScanState()
    object Scanning : ScanState()
    data class Result(
        val foodName: String,
        val calories: Int,
        val carbs: Double,
        val protein: Double,
        val fat: Double
    ) : ScanState()
} 