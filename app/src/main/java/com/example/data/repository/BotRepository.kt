package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.BotSettingsEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.FaqEntity
import com.example.data.local.WebhookLogEntity
import com.example.data.remote.ApiClient
import com.example.data.remote.WhatsAppSendMessageRequest
import com.example.data.remote.WhatsAppTextMessage
import com.example.domain.AiSupportEngine
import com.example.domain.BotReplyResult
import com.example.domain.VerificationResult
import com.example.domain.WebhookSimulator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class BotRepository(private val db: AppDatabase) {

    val settingsFlow: Flow<BotSettingsEntity?> = db.botSettingsDao().getSettingsFlow()
    val faqsFlow: Flow<List<FaqEntity>> = db.faqDao().getAllFaqs()
    val webhookLogsFlow: Flow<List<WebhookLogEntity>> = db.webhookLogDao().getAllLogs()
    val totalMessagesCount: Flow<Int> = db.chatMessageDao().getTotalMessageCount()
    val escalationsCount: Flow<Int> = db.chatMessageDao().getEscalationCount()
    val escalatedMessagesFlow: Flow<List<ChatMessageEntity>> = db.chatMessageDao().getEscalatedMessages()

    fun getChatMessages(sessionId: String): Flow<List<ChatMessageEntity>> {
        return db.chatMessageDao().getMessagesForSession(sessionId)
    }

    suspend fun getSettings(): BotSettingsEntity {
        return db.botSettingsDao().getSettings() ?: BotSettingsEntity()
    }

    suspend fun updateSettings(settings: BotSettingsEntity) {
        db.botSettingsDao().insertOrUpdate(settings)
    }

    suspend fun addFaq(faq: FaqEntity): Long {
        return db.faqDao().insertFaq(faq)
    }

    suspend fun updateFaq(faq: FaqEntity) {
        db.faqDao().updateFaq(faq)
    }

    suspend fun deleteFaq(faq: FaqEntity) {
        db.faqDao().deleteFaq(faq)
    }

    suspend fun clearChatSession(sessionId: String) {
        db.chatMessageDao().clearSession(sessionId)
    }

    suspend fun resolveEscalation(messageId: Long) {
        db.chatMessageDao().resolveEscalation(messageId)
    }

    suspend fun clearWebhookLogs() {
        db.webhookLogDao().clearLogs()
    }

    suspend fun simulateVerification(token: String, challenge: String): VerificationResult {
        val settings = getSettings()
        val result = WebhookSimulator.verifyWebhook(token, challenge, settings.verifyToken)
        db.webhookLogDao().insertLog(
            WebhookLogEntity(
                method = "GET",
                endpoint = "/webhook?hub.mode=subscribe&hub.verify_token=$token",
                status = result.statusCode,
                requestPayload = "hub.verify_token=$token&hub.challenge=$challenge",
                responsePayload = result.responseBody,
                executionTimeMs = 12L
            )
        )
        return result
    }

    suspend fun processCustomerMessage(
        sessionId: String,
        customerName: String,
        messageText: String,
        sendRealApiIfConfigured: Boolean = false
    ): BotReplyResult {
        val startTime = System.currentTimeMillis()
        val settings = getSettings()
        val faqs = db.faqDao().getEnabledFaqs()

        // 1. Save customer message
        db.chatMessageDao().insertMessage(
            ChatMessageEntity(
                sessionId = sessionId,
                customerName = customerName,
                sender = "CUSTOMER",
                text = messageText,
                timestamp = System.currentTimeMillis()
            )
        )

        // 2. Generate AI reply
        val replyResult = AiSupportEngine.generateReply(messageText, settings, faqs)

        // 3. Save bot reply
        db.chatMessageDao().insertMessage(
            ChatMessageEntity(
                sessionId = sessionId,
                customerName = customerName,
                sender = "BOT",
                text = replyResult.replyText,
                timestamp = System.currentTimeMillis(),
                isEscalated = replyResult.isEscalated,
                detectedLanguage = replyResult.detectedLanguage
            )
        )

        // 4. If configured with Meta WA_TOKEN, attempt real WhatsApp Cloud API send
        var outboundStatus = 200
        var outboundResponseStr = "{\"messaging_product\":\"whatsapp\",\"messages\":[{\"id\":\"wamid.simulated\"}]}"

        if (sendRealApiIfConfigured && settings.waToken.isNotBlank() && settings.phoneId.isNotBlank()) {
            try {
                val sanitizedPhone = sessionId.replace("+", "").replace(" ", "").trim()
                val apiResp = ApiClient.whatsAppService.sendMessage(
                    phoneId = settings.phoneId,
                    authHeader = "Bearer ${settings.waToken}",
                    request = WhatsAppSendMessageRequest(
                        to = sanitizedPhone,
                        text = WhatsAppTextMessage(body = replyResult.replyText)
                    )
                )
                outboundStatus = apiResp.code()
                outboundResponseStr = if (apiResp.isSuccessful) {
                    apiResp.body()?.toString() ?: "Message sent to WhatsApp"
                } else {
                    apiResp.errorBody()?.string() ?: "Failed status $outboundStatus"
                }
            } catch (e: Exception) {
                outboundStatus = 500
                outboundResponseStr = "Error: ${e.message}"
            }
        }

        // 5. Log webhook event
        val duration = System.currentTimeMillis() - startTime
        val event = WebhookSimulator.buildIncomingWebhookPayload(
            customerPhone = sessionId,
            customerName = customerName,
            messageText = messageText,
            phoneId = settings.phoneId
        )

        db.webhookLogDao().insertLog(
            WebhookLogEntity(
                method = "POST",
                endpoint = "/webhook",
                status = outboundStatus,
                requestPayload = event.incomingJson,
                responsePayload = outboundResponseStr,
                customerPhone = sessionId,
                executionTimeMs = duration
            )
        )

        return replyResult
    }
}
