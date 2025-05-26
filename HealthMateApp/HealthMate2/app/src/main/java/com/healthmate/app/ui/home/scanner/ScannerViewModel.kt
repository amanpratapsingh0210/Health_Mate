package com.healthmate.app.ui.home.scanner

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthmate.app.data.AnalysisResult
import com.healthmate.app.data.FoodApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.source
import okio.BufferedSink

private const val TAG = "ScannerViewModel"

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val foodApiService: FoodApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Initial)
    val scanState: StateFlow<ScanState> = _scanState

    init {
        _scanState.value = ScanState.Initial
    }

    fun analyzeImage(imageUri: Uri) {
        Log.d(TAG, "analyzeImage called with Uri: $imageUri")
        _scanState.value = ScanState.Scanning

        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                if (inputStream == null) {
                    _scanState.value = ScanState.Error("Could not open image stream.")
                    Log.e(TAG, "Could not open input stream for Uri: $imageUri")
                    return@launch
                }

                val imageBytes = inputStream.use { input ->
                    input.readBytes()
                }

                val requestBody = object : RequestBody() {
                    override fun contentType() = "image/*".toMediaTypeOrNull()

                    override fun contentLength() = imageBytes.size.toLong()

                    override fun writeTo(sink: okio.BufferedSink) {
                        sink.write(imageBytes)
                    }
                }

                val filename = getFileNameFromUri(imageUri) ?: "upload.jpg"

                val imagePart = MultipartBody.Part.createFormData("image", filename, requestBody)

                Log.d(TAG, "Making API call to analyze image.")

                val results = foodApiService.analyzeImage(imagePart)
                Log.d(TAG, "API call finished successfully.")

                if (!results.isNullOrEmpty()) {
                    Log.d(TAG, "API Success: ${results.size} items detected. Results: $results")
                    _scanState.value = ScanState.ResultList(results)
                } else {
                    Log.d(TAG, "API Success: No food items detected.")
                    _scanState.value = ScanState.Error("No food items detected.")
                }

            } catch (e: IOException) {
                Log.e(TAG, "Network Error during API call", e)
                _scanState.value = ScanState.Error("Network error: ${e.message}")
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP Error during API call", e)
                _scanState.value = ScanState.Error("HTTP error: ${e.code()}")
            } catch (e: Exception) {
                Log.e(TAG, "Unknown Error during API call", e)
                _scanState.value = ScanState.Error("An unexpected error occurred.")
            }
        }
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        var name: String? = null
        if (uri.scheme == "file") {
            name = uri.lastPathSegment
        } else if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        name = it.getString(nameIndex)
                    }
                }
            }
        }
        return name
    }

    fun resetScanState() {
        Log.d(TAG, "Resetting scan state to Initial.")
        _scanState.value = ScanState.Initial
    }
}

sealed class ScanState {
    object Initial : ScanState()
    object Scanning : ScanState()
    data class ResultList(val results: List<AnalysisResult>) : ScanState()
    data class Error(val message: String) : ScanState()
} 