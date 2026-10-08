package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.BotSettingsEntity
import com.example.data.local.FaqEntity
import com.example.domain.AiSupportEngine
import com.example.domain.CodeExporter
import com.example.domain.WebhookSimulator
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("WaBot Studio", appName)
    }

    @Test
    fun `test webhook verification handshake`() {
        val secretToken = "my_wabot_verify_token_786"
        val challenge = "1158201444"

        // Correct token
        val successResult = WebhookSimulator.verifyWebhook(
            verifyTokenParam = secretToken,
            challengeParam = challenge,
            configuredToken = secretToken
        )
        assertEquals(200, successResult.statusCode)
        assertEquals(challenge, successResult.responseBody)
        assertTrue(successResult.isSuccess)

        // Invalid token
        val failureResult = WebhookSimulator.verifyWebhook(
            verifyTokenParam = "wrong_token",
            challengeParam = challenge,
            configuredToken = secretToken
        )
        assertEquals(403, failureResult.statusCode)
        assertEquals("forbidden", failureResult.responseBody)
        assertFalse(failureResult.isSuccess)
    }

    @Test
    fun `test bilingual AI support engine escalation`() = runBlocking {
        val settings = BotSettingsEntity(
            humanEscalationMessage = "Main aapko human agent se connect karta hoon."
        )
        val faqs = listOf(
            FaqEntity(
                question = "Biryani rate kya hai?",
                answer = "Rs. 380 single",
                category = "Menu",
                keywords = "biryani, rate, price"
            )
        )

        // Inquiry asking for human agent
        val escalationReply = AiSupportEngine.generateReply(
            userMessage = "Main human agent se baat karna chahta hoon",
            settings = settings,
            faqs = faqs
        )
        assertTrue(escalationReply.isEscalated)
        assertTrue(escalationReply.replyText.contains("human agent"))

        // Standard menu inquiry matching FAQ
        val menuReply = AiSupportEngine.generateReply(
            userMessage = "Biryani ka rate batao bhai",
            settings = settings,
            faqs = faqs
        )
        assertTrue(menuReply.replyText.contains("380"))
    }

    @Test
    fun `test code exporter generates python script`() {
        val settings = BotSettingsEntity(
            businessName = "Karachi Spice & Grill",
            verifyToken = "token_xyz"
        )
        val code = CodeExporter.generateFlaskPythonCode(settings, emptyList())
        assertTrue(code.contains("Flask(__name__)"))
        assertTrue(code.contains("token_xyz"))
        assertTrue(code.contains("Karachi Spice & Grill"))
    }
}
