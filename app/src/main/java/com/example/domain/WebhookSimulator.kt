package com.example.domain

data class VerificationResult(
    val statusCode: Int,
    val responseBody: String,
    val isSuccess: Boolean,
    val details: String
)

data class SimulatedWebhookEvent(
    val incomingJson: String,
    val fromPhone: String,
    val customerName: String,
    val messageText: String,
    val messageId: String,
    val timestamp: Long,
    val outboundPayloadJson: String
)

object WebhookSimulator {

    fun verifyWebhook(
        verifyTokenParam: String,
        challengeParam: String,
        configuredToken: String
    ): VerificationResult {
        return if (verifyTokenParam == configuredToken && configuredToken.isNotBlank()) {
            VerificationResult(
                statusCode = 200,
                responseBody = challengeParam.ifBlank { "1158201444" },
                isSuccess = true,
                details = "Meta Webhook handshake verified successfully! Token matches."
            )
        } else {
            VerificationResult(
                statusCode = 403,
                responseBody = "forbidden",
                isSuccess = false,
                details = "Verification failed: hub.verify_token mismatch or empty."
            )
        }
    }

    fun buildIncomingWebhookPayload(
        customerPhone: String,
        customerName: String,
        messageText: String,
        phoneId: String
    ): SimulatedWebhookEvent {
        val sanitizedPhone = customerPhone.replace("+", "").replace(" ", "").trim()
        val now = System.currentTimeMillis() / 1000
        val messageId = "wamid.HBgL${System.currentTimeMillis()}AB"

        val rawIncomingJson = """
{
  "entry": [
    {
      "id": "$phoneId",
      "changes": [
        {
          "field": "messages",
          "value": {
            "messaging_product": "whatsapp",
            "metadata": {
              "display_phone_number": "+92 300 1234567",
              "phone_number_id": "$phoneId"
            },
            "contacts": [
              {
                "profile": {
                  "name": "$customerName"
                },
                "wa_id": "$sanitizedPhone"
              }
            ],
            "messages": [
              {
                "from": "$sanitizedPhone",
                "id": "$messageId",
                "timestamp": "$now",
                "type": "text",
                "text": {
                  "body": "$messageText"
                }
              }
            ]
          }
        }
      ]
    }
  ]
}
        """.trimIndent()

        val outboundJson = """
{
  "messaging_product": "whatsapp",
  "recipient_type": "individual",
  "to": "$sanitizedPhone",
  "type": "text",
  "text": {
    "body": "..."
  }
}
        """.trimIndent()

        return SimulatedWebhookEvent(
            incomingJson = rawIncomingJson,
            fromPhone = sanitizedPhone,
            customerName = customerName,
            messageText = messageText,
            messageId = messageId,
            timestamp = now * 1000,
            outboundPayloadJson = outboundJson
        )
    }
}
