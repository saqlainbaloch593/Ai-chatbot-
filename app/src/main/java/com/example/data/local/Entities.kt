package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bot_settings")
data class BotSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val businessName: String = "Karachi Spice & Grill",
    val businessIndustry: String = "Food & Restaurant",
    val businessPhone: String = "+92 300 1234567",
    val businessHours: String = "12:00 PM - 1:00 AM (Daily)",
    val businessAddress: String = "Clifton Block 4, Karachi, Pakistan",
    val currency: String = "PKR",
    val verifyToken: String = "my_wabot_verify_token_786",
    val waToken: String = "",
    val phoneId: String = "109876543210987",
    val modelName: String = "gemini-3.5-flash",
    val customSystemPrompt: String = """Tum [Business Name] ke customer support assistant ho.
FAQs, prices, timings yahan likho...
Customer jis zubaan mein likhe (Roman Urdu/English) usi mein chhota jawab do.
Agar jawab nahi pata to kaho: 'Main aapko human agent se connect karta hoon.'""".trimIndent(),
    val humanEscalationMessage: String = "Main aapko human agent se connect karta hoon.",
    val geminiApiKeyOverride: String = "",
    val autoEscalateUnsure: Boolean = true,
    val isBotActive: Boolean = true
)

@Entity(tableName = "faqs")
data class FaqEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val question: String,
    val answer: String,
    val category: String = "General",
    val keywords: String = "",
    val isEnabled: Boolean = true
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String, // Customer phone number, e.g. "+92 300 9876543"
    val customerName: String = "Valued Customer",
    val sender: String, // "CUSTOMER", "BOT", "HUMAN_AGENT"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "READ", // "SENT", "DELIVERED", "READ"
    val isEscalated: Boolean = false,
    val detectedLanguage: String = "Roman Urdu"
)

@Entity(tableName = "webhook_logs")
data class WebhookLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val method: String, // "GET" or "POST"
    val endpoint: String = "/webhook",
    val status: Int = 200,
    val requestPayload: String = "",
    val responsePayload: String = "",
    val customerPhone: String? = null,
    val executionTimeMs: Long = 0L
)
