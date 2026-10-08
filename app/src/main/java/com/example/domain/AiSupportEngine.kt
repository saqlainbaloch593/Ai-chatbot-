package com.example.domain

import com.example.BuildConfig
import com.example.data.local.BotSettingsEntity
import com.example.data.local.FaqEntity
import com.example.data.remote.ApiClient
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiGenConfig
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

data class BotReplyResult(
    val replyText: String,
    val isEscalated: Boolean,
    val detectedLanguage: String,
    val poweredBy: String // "Gemini 3.5 Flash" or "Smart Local Engine"
)

object AiSupportEngine {

    suspend fun generateReply(
        userMessage: String,
        settings: BotSettingsEntity,
        faqs: List<FaqEntity>
    ): BotReplyResult = withContext(Dispatchers.IO) {
        val detectedLang = detectLanguage(userMessage)

        // Check if user is asking for a human agent or expressing strong complaint
        if (shouldDirectlyEscalate(userMessage, settings)) {
            val escalationMsg = if (detectedLang == "English") {
                "I am connecting you with a human agent right now. Please hold on."
            } else {
                settings.humanEscalationMessage
            }
            return@withContext BotReplyResult(
                replyText = escalationMsg,
                isEscalated = true,
                detectedLanguage = detectedLang,
                poweredBy = "Rule Engine"
            )
        }

        val apiKey = when {
            settings.geminiApiKeyOverride.isNotBlank() -> settings.geminiApiKeyOverride.trim()
            BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" -> BuildConfig.GEMINI_API_KEY
            else -> ""
        }

        if (apiKey.isNotBlank()) {
            try {
                val fullSystemPrompt = buildSystemPrompt(settings, faqs)
                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            role = "user",
                            parts = listOf(GeminiPart(text = userMessage))
                        )
                    ),
                    systemInstruction = GeminiContent(
                        parts = listOf(GeminiPart(text = fullSystemPrompt))
                    ),
                    generationConfig = GeminiGenConfig(
                        temperature = 0.35f,
                        maxOutputTokens = 250
                    )
                )

                val response = ApiClient.geminiService.generateContent(
                    model = settings.modelName.ifBlank { "gemini-3.5-flash" },
                    apiKey = apiKey,
                    request = request
                )

                val candidateText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                if (!candidateText.isNullOrBlank()) {
                    val isEscalated = isEscalationResponse(candidateText, settings)
                    return@withContext BotReplyResult(
                        replyText = candidateText,
                        isEscalated = isEscalated,
                        detectedLanguage = detectedLang,
                        poweredBy = "Gemini 3.5 Flash"
                    )
                }
            } catch (_: Exception) {
                // If API call encounters network error or invalid key, fall through to smart local engine
            }
        }

        // Smart Local Heuristic Engine (Offline / Instant bilingual support)
        val localReply = generateLocalBilingualReply(userMessage, settings, faqs, detectedLang)
        val isEscalated = isEscalationResponse(localReply, settings)

        BotReplyResult(
            replyText = localReply,
            isEscalated = isEscalated,
            detectedLanguage = detectedLang,
            poweredBy = "Smart Local Engine"
        )
    }

    fun buildSystemPrompt(settings: BotSettingsEntity, faqs: List<FaqEntity>): String {
        val faqBuilder = StringBuilder()
        faqs.filter { it.isEnabled }.forEachIndexed { idx, faq ->
            faqBuilder.append("${idx + 1}. Q: ${faq.question}\n   A: ${faq.answer}\n")
        }

        return """
Tum ${settings.businessName} (${settings.businessIndustry}) ke customer support assistant ho.
Hamara Address: ${settings.businessAddress}
Hamare Timings: ${settings.businessHours}
Contact Number: ${settings.businessPhone}
Currency: ${settings.currency}

BUSINESS FAQS, PRICES & DETAILS:
$faqBuilder

IMPORTANT RULES:
1. Customer jis zubaan mein likhe (Roman Urdu ya English) usi zubaan mein concise, polite aur direct jawab do.
2. Jawab zyada lamba mat karo (2-3 sentences max), WhatsApp chat ke mutabiq natural rakho.
3. Roman Urdu mein baat kare to casual natural Pakistani Roman Urdu use karo (jaise 'Ji bilkul!', 'Hamara time...', 'Aapka order...').
4. Agar customer ke sawal ka jawab upar diye gaye FAQs mein mojood na ho, ya customer complaint kare, ya human agent se baat karna chahe to strictly kaho: '${settings.humanEscalationMessage}'.
        """.trimIndent()
    }

    private fun detectLanguage(text: String): String {
        val lower = text.lowercase(Locale.ROOT)
        val romanUrduMarkers = listOf(
            "kya", "hai", "hain", "bhai", "batao", "kahan", "kitna", "kitne",
            "chahiye", "karahi", "biryani", "aao", "salam", "assalam", "walaikum",
            "shukriya", "mein", "hum", "aap", "tum", "mera", "meri", "hoga", "ho"
        )
        val urduScriptRegex = Regex("[\\u0600-\\u06FF]")
        if (urduScriptRegex.containsMatchIn(text)) return "Urdu"

        val matchCount = romanUrduMarkers.count { lower.contains(it) }
        return if (matchCount >= 1) "Roman Urdu" else "English"
    }

    private fun shouldDirectlyEscalate(text: String, settings: BotSettingsEntity): Boolean {
        val lower = text.lowercase(Locale.ROOT)
        val keywords = listOf(
            "human", "agent", "insaan", "representative", "operator", "manager",
            "talk to person", "complaint", "shikayat", "refund", "kharab",
            "dhoka", "fraud", "scam", "court", "call me", "call karo", "baat karo"
        )
        return keywords.any { lower.contains(it) }
    }

    private fun isEscalationResponse(reply: String, settings: BotSettingsEntity): Boolean {
        val lower = reply.lowercase(Locale.ROOT)
        return lower.contains("human agent") ||
                lower.contains("connect karta hoon") ||
                lower.contains("connecting you with a human") ||
                lower.contains("shikayat darj") ||
                lower.contains(settings.humanEscalationMessage.lowercase(Locale.ROOT))
    }

    private fun generateLocalBilingualReply(
        query: String,
        settings: BotSettingsEntity,
        faqs: List<FaqEntity>,
        lang: String
    ): String {
        val q = query.lowercase(Locale.ROOT)

        // Greetings
        if (q.contains("salam") || q.contains("aoa") || q.contains("hello") || q.contains("hi") || q.contains("hey")) {
            return if (lang == "English") {
                "Hello! Welcome to ${settings.businessName}. How can I assist you with our menu, prices, or timings today?"
            } else {
                "Walaikum Assalam! ${settings.businessName} mein khush-aamdeed. Main aapki kya madad kar sakta hoon?"
            }
        }

        // Thanks
        if (q.contains("shukriya") || q.contains("thank") || q.contains("jazakallah")) {
            return if (lang == "English") {
                "You are most welcome! Let us know if you need anything else."
            } else {
                "Bohot shukriya! Koi aur sawal ho to zaroor batayein."
            }
        }

        // FAQ Matcher
        var bestFaq: FaqEntity? = null
        var maxMatches = 0

        for (faq in faqs.filter { it.isEnabled }) {
            val keyTokens = faq.keywords.lowercase(Locale.ROOT).split(",", " ").map { it.trim() }.filter { it.length > 2 }
            val questionTokens = faq.question.lowercase(Locale.ROOT).split(" ").map { it.trim() }.filter { it.length > 3 }
            val allTokens = keyTokens + questionTokens

            val matches = allTokens.count { token -> q.contains(token) }
            if (matches > maxMatches) {
                maxMatches = matches
                bestFaq = faq
            }
        }

        if (bestFaq != null && maxMatches >= 1) {
            return if (lang == "English") {
                bestFaq.answer
            } else {
                "Ji! ${bestFaq.answer}"
            }
        }

        // Timings heuristic
        if (q.contains("timing") || q.contains("time") || q.contains("kab") || q.contains("hours") || q.contains("open")) {
            return if (lang == "English") {
                "We are open daily from ${settings.businessHours}."
            } else {
                "Hamara restaurant rozana ${settings.businessHours} tak khula rehta hai."
            }
        }

        // Location heuristic
        if (q.contains("address") || q.contains("location") || q.contains("kahan") || q.contains("kidhar")) {
            return if (lang == "English") {
                "We are located at ${settings.businessAddress}."
            } else {
                "Hamara address hai: ${settings.businessAddress}."
            }
        }

        // Phone / Contact
        if (q.contains("number") || q.contains("phone") || q.contains("contact") || q.contains("raabta")) {
            return if (lang == "English") {
                "You can reach us at ${settings.businessPhone}."
            } else {
                "Aap hamare phone number ${settings.businessPhone} par contact kar sakte hain."
            }
        }

        // Unknown / Escalate
        return settings.humanEscalationMessage
    }
}
