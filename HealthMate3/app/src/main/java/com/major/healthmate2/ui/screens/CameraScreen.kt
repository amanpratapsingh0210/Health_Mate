package com.major.healthmate2.ui.screens

import android.net.Uri
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.major.healthmate2.viewmodel.CameraState
import com.major.healthmate2.viewmodel.CameraViewModel

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    onRequestPermission: () -> Unit,
    hasPermission: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraState by viewModel.cameraState.collectAsState()
    val capturedImageUri by viewModel.capturedImageUri.collectAsState()
    val mlResults by viewModel.mlResults.collectAsState()

    // Request permission if not granted
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            onRequestPermission()
        }
    }

    // Initialize camera when permission is granted
    LaunchedEffect(hasPermission) {
        if (hasPermission && cameraState == CameraState.Initial) {
            viewModel.startCamera(context, lifecycleOwner, PreviewView(context))
        }
    }

    // Function to handle retaking picture
    val onRetake = {
        viewModel.startCamera(context, lifecycleOwner, PreviewView(context))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        when {
            !hasPermission -> {
                Text("Camera permission is required")
                Button(onClick = onRequestPermission) {
                    Text("Grant Permission")
                }
            }
            cameraState is CameraState.Initial -> {
                CircularProgressIndicator()
            }
            cameraState is CameraState.Ready -> {
                // Camera Preview with border and rounded corners
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 4.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .shadow(8.dp, RoundedCornerShape(16.dp))
                ) {
                    CameraPreview(
                        modifier = Modifier.fillMaxSize(),
                        onInitializeCamera = { previewView ->
                            viewModel.startCamera(context, lifecycleOwner, previewView)
                        },
                        onCaptureClick = {
                            viewModel.captureImage(context)
                        }
                    )
                }
                
                // Results placeholder when no image is captured
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Capture an image to see results",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            cameraState is CameraState.ProcessingComplete -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    capturedImageUri?.let { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = "Captured image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ML Results",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        mlResults.forEach { result ->
                            Text(
                                text = result,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = onRetake,
                            modifier = Modifier
                                .shadow(4.dp, RoundedCornerShape(24.dp)),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text("Take Another Picture")
                        }
                    }
                }
            }
            cameraState is CameraState.Error -> {
                Text(
                    text = (cameraState as CameraState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
                Button(
                    onClick = { viewModel.startCamera(context, lifecycleOwner, PreviewView(context)) },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onInitializeCamera: (PreviewView) -> Unit,
    onCaptureClick: () -> Unit
) {
    Box(modifier = modifier) {
        AndroidView(
            factory = { context ->
                PreviewView(context).also { previewView ->
                    onInitializeCamera(previewView)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Enhanced Capture Button
        Button(
            onClick = onCaptureClick,
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    spotColor = MaterialTheme.colorScheme.primary
                ),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun CapturedImagePreview(
    imageUri: Uri?,
    mlResults: List<String>,
    onRetake: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        imageUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Captured image",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "ML Results:",
            style = MaterialTheme.typography.titleMedium
        )
        
        mlResults.forEach { result ->
            Text(text = result)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRetake,
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Take Another Picture")
        }
    }
} 