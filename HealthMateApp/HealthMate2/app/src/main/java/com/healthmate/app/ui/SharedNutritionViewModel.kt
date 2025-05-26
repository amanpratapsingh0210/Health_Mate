package com.healthmate.app.ui

import androidx.lifecycle.ViewModel
import com.healthmate.app.data.AnalysisResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SharedNutritionViewModel @Inject constructor() : ViewModel() {

    private val _analysisResults = MutableStateFlow<List<AnalysisResult>>(emptyList())
    val analysisResults: StateFlow<List<AnalysisResult>> = _analysisResults

    fun setAnalysisResults(results: List<AnalysisResult>) {
        _analysisResults.value = results
    }

    fun clearResults() {
        _analysisResults.value = emptyList()
    }
} 