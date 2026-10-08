package com.example.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface WhatsAppApiService {
    @POST("v21.0/{phoneId}/messages")
    suspend fun sendMessage(
        @Path("phoneId") phoneId: String,
        @Header("Authorization") authHeader: String,
        @Body request: WhatsAppSendMessageRequest
    ): Response<WhatsAppSendMessageResponse>
}
