package com.example.myapplication.viewmodel

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.utils.UiState
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AIViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    @SuppressLint("RestrictedApi")
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-pro",
        apiKey = "AIzaSyCtKxNML--te6DJZ-CMwXZ5Dl6ry0Gwppo"
    )

    fun sendMessage(
        message: String,
        onResponse: (String) -> Unit // Callback to handle the response
    ) {
        // Customize the prompt for health-related questions
        val customizedMessage = "Please act like a health assistant but remember the context of the conversation provide a brief answer to the following health question: $message"
        Log.d("AIViewModel", "Sending message: $customizedMessage")
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(customizedMessage)
                    }
                )
                Log.d("AIViewModel", "Response: $response") // Log the entire response
                withContext(Dispatchers.Main) {
                    response.text?.let { outputContent ->
                        onResponse(outputContent)
                    } ?: run {
                        onResponse("Empty response from the model.")
                    }
                }
            } catch (e: Exception) {
                Log.e("AIViewModel", "Error occurred: ${e.localizedMessage}", e) // Log the full exception
                withContext(Dispatchers.Main) {
                    onResponse(e.localizedMessage ?: "Something unexpected happened.")
                }
            }
        }
    }

    // Function to resize the bitmap
    fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val aspectRatio = width.toFloat() / height.toFloat()
        
        var newWidth = maxWidth
        var newHeight = maxHeight
        
        if (width > height) {
            newHeight = (maxWidth / aspectRatio).toInt()
        } else {
            newWidth = (maxHeight * aspectRatio).toInt()
        }
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}