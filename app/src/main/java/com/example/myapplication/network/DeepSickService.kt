package com.example.myapplication.network

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Body

interface DeepSickService {
    @POST("chat")
    suspend fun sendMessage(@Body message: ChatRequest): Response<ChatResponse>
}

data class ChatRequest(
    val message: String
)

data class ChatResponse(
    val reply: String
) 