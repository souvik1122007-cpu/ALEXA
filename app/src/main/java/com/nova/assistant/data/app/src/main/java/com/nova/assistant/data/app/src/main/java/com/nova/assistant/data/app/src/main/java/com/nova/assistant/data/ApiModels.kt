
package com.nova.assistant.data

data class ApiMessage(
    val role: String,
    val content: String
)

data class ChatRequest(
    val messages: List<ApiMessage>
)

data class ChatResponse(
    val reply: String
)

data class HealthResponse(
    val status: String
)

data class ApiError(
    val detail: String?
)
