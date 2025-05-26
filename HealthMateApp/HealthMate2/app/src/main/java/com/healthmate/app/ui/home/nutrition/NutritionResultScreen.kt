package com.healthmate.app.ui.home.nutrition

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.healthmate.app.data.AnalysisResult
import com.healthmate.app.data.NutritionInfo
import com.healthmate.app.ui.SharedNutritionViewModel
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio

@Composable
fun NutritionResultScreen(
    viewModel: SharedNutritionViewModel = hiltViewModel()
) {
    val analysisResults by viewModel.analysisResults.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Nutrition Analysis Results",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Divider()
        if (analysisResults.isEmpty()) {
            Text("No results found.")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(analysisResults) { result ->
                    NutritionResultItem(result = result)
                }
            }
        }
    }
}

@Composable
fun NutritionResultItem(result: AnalysisResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val imageUrl = "http://192.168.153.242:5000/output_items/${result.filename}"
            val painter = rememberAsyncImagePainter(imageUrl)
            Image(
                painter = painter,
                contentDescription = "Preview of ${result.label}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )

            Text(
                text = result.label,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            result.nutrition?.let { nutrition ->
                Text(text = "Calories: ${nutrition.calories} kcal", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Protein: ${nutrition.protein} g", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Carbs: ${nutrition.carbs} g", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Fat: ${nutrition.fat} g", style = MaterialTheme.typography.bodyMedium)
            } ?: run {
                Text(text = "Nutrition information not available.", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
} 