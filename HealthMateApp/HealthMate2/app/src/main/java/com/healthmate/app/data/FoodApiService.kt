package com.healthmate.app.data

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class AnalysisResult(
    val filename: String,
    val label: String,
    val nutrition: NutritionInfo?
)

data class NutritionInfo(
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double
)

interface FoodApiService {
    @Multipart
    @POST("analyze") // Assuming your Flask endpoint is /analyze
    suspend fun analyzeImage(@Part image: MultipartBody.Part): List<AnalysisResult>
} 