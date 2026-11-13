
package com.nova.assistant.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface NovaApi {

    @GET("health")
    suspend fun health(): HealthResponse

    @POST("chat")
    suspend fun sendMessage(
        @Header("Authorization") authorization: String,
        @Body request: ChatRequest
    ): Response<ChatResponse>
}
