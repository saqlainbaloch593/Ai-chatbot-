package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// --- Gemini REST API Models ---

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null,
    val generationConfig: GeminiGenConfig? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val role: String? = null,
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiGenConfig(
    val temperature: Float? = 0.4f,
    val maxOutputTokens: Int? = 300
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent? = null
)

// --- WhatsApp Cloud API Models ---

@JsonClass(generateAdapter = true)
data class WhatsAppSendMessageRequest(
    @param:Json(name = "messaging_product") val messagingProduct: String = "whatsapp",
    @param:Json(name = "recipient_type") val recipientType: String = "individual",
    val to: String,
    val type: String = "text",
    val text: WhatsAppTextMessage
)

@JsonClass(generateAdapter = true)
data class WhatsAppTextMessage(
    @param:Json(name = "preview_url") val previewUrl: Boolean = false,
    val body: String
)

@JsonClass(generateAdapter = true)
data class WhatsAppSendMessageResponse(
    @param:Json(name = "messaging_product") val messagingProduct: String? = null,
    val contacts: List<WhatsAppContactResponse>? = null,
    val messages: List<WhatsAppMessageResponseItem>? = null
)

@JsonClass(generateAdapter = true)
data class WhatsAppContactResponse(
    val input: String? = null,
    @param:Json(name = "wa_id") val waId: String? = null
)

@JsonClass(generateAdapter = true)
data class WhatsAppMessageResponseItem(
    val id: String? = null
)

// --- Meta Webhook JSON Models ---

@JsonClass(generateAdapter = true)
data class MetaWebhookPayload(
    val entry: List<MetaEntry>? = null
)

@JsonClass(generateAdapter = true)
data class MetaEntry(
    val id: String? = null,
    val changes: List<MetaChange>? = null
)

@JsonClass(generateAdapter = true)
data class MetaChange(
    val value: MetaChangeValue? = null,
    val field: String? = null
)

@JsonClass(generateAdapter = true)
data class MetaChangeValue(
    @param:Json(name = "messaging_product") val messagingProduct: String? = null,
    val metadata: MetaMetadata? = null,
    val contacts: List<MetaContact>? = null,
    val messages: List<MetaIncomingMessage>? = null
)

@JsonClass(generateAdapter = true)
data class MetaMetadata(
    @param:Json(name = "display_phone_number") val displayPhoneNumber: String? = null,
    @param:Json(name = "phone_number_id") val phoneNumberId: String? = null
)

@JsonClass(generateAdapter = true)
data class MetaContact(
    val profile: MetaProfile? = null,
    @param:Json(name = "wa_id") val waId: String? = null
)

@JsonClass(generateAdapter = true)
data class MetaProfile(
    val name: String? = null
)

@JsonClass(generateAdapter = true)
data class MetaIncomingMessage(
    val from: String? = null,
    val id: String? = null,
    val timestamp: String? = null,
    val type: String? = null,
    val text: MetaTextBody? = null
)

@JsonClass(generateAdapter = true)
data class MetaTextBody(
    val body: String? = null
)
