package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Webhook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.WebhookSimulator
import com.example.ui.theme.CodeBgDark
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.EscalationRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WhatsAppDarkTeal
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTeal
import com.example.ui.viewmodel.BotViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WebhookTesterScreen(
    viewModel: BotViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    val webhookLogs by viewModel.webhookLogs.collectAsState()
    val verificationResult by viewModel.lastVerificationResult.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Verification GET, 1: Incoming Message POST, 2: Logs

    // Tab 0 State (GET Verification)
    var testVerifyToken by remember(settings) { mutableStateOf(settings.verifyToken) }
    var testChallenge by remember { mutableStateOf("1158201444") }

    // Tab 1 State (POST Incoming Message)
    var customerPhone by remember { mutableStateOf("923001234567") }
    var customerName by remember { mutableStateOf("Tariq Jameel") }
    var incomingMessageText by remember { mutableStateOf("Bhai biryani ka kya rate hai?") }
    var lastSimulatedPostResult by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("webhook_tester_screen")
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("GET Verify", fontSize = 13.sp) },
                icon = { Icon(Icons.Default.Webhook, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("POST Message", fontSize = 13.sp) },
                icon = { Icon(Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Logs (${webhookLogs.size})", fontSize = 13.sp) },
                icon = { Icon(Icons.Default.BugReport, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // Meta Webhook GET Handshake Simulator
                    item {
                        Text(
                            text = "Meta Webhook Handshake (GET /webhook)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "When you register your webhook in the Meta App Dashboard, Meta sends a GET request to verify your VERIFY_TOKEN.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = "subscribe",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("hub.mode") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = testVerifyToken,
                                    onValueChange = { testVerifyToken = it },
                                    label = { Text("hub.verify_token (Incoming)") },
                                    supportingText = { Text("Configured in Bot: ${settings.verifyToken}") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = testChallenge,
                                    onValueChange = { testChallenge = it },
                                    label = { Text("hub.challenge") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Button(
                                    onClick = {
                                        viewModel.testWebhookVerification(testVerifyToken, testChallenge)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("test_handshake_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppTeal)
                                ) {
                                    Icon(imageVector = Icons.Default.Webhook, contentDescription = null)
                                    Spacer(modifier = Modifier.size(8.dp))
                                    Text("Simulate Meta Handshake")
                                }
                            }
                        }
                    }

                    if (verificationResult != null) {
                        item {
                            val res = verificationResult!!
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (res.isSuccess) SuccessGreen.copy(alpha = 0.12f) else EscalationRed.copy(alpha = 0.12f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (res.isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                                            contentDescription = null,
                                            tint = if (res.isSuccess) SuccessGreen else EscalationRed
                                        )
                                        Text(
                                            text = "HTTP ${res.statusCode} ${if (res.isSuccess) "OK" else "FORBIDDEN"}",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (res.isSuccess) SuccessGreen else EscalationRed
                                        )
                                    }
                                    Text(text = res.details, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = "Server Response Body: \"${res.responseBody}\"",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Meta Webhook POST Message Simulator
                    item {
                        Text(
                            text = "Incoming WhatsApp Message (POST /webhook)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Simulates incoming WhatsApp JSON payload: entry[0].changes[0].value.messages[0]. Passes through AI and prepares outbound reply.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = customerPhone,
                                        onValueChange = { customerPhone = it },
                                        label = { Text("Customer Number (from)") },
                                        modifier = Modifier.weight(1.2f)
                                    )
                                    OutlinedTextField(
                                        value = customerName,
                                        onValueChange = { customerName = it },
                                        label = { Text("Name") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                OutlinedTextField(
                                    value = incomingMessageText,
                                    onValueChange = { incomingMessageText = it },
                                    label = { Text("Incoming WhatsApp Text (body)") },
                                    placeholder = { Text("e.g. Biryani ka rate kya hai?") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Button(
                                    onClick = {
                                        viewModel.switchCustomer(customerPhone, customerName)
                                        viewModel.sendCustomerMessage(incomingMessageText)
                                        val event = WebhookSimulator.buildIncomingWebhookPayload(
                                            customerPhone, customerName, incomingMessageText, settings.phoneId
                                        )
                                        lastSimulatedPostResult = event.incomingJson
                                        Toast.makeText(context, "Webhook executed & AI responded!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("simulate_post_webhook_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppTeal)
                                ) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null)
                                    Spacer(modifier = Modifier.size(8.dp))
                                    Text("Post Webhook Event & Run AI")
                                }
                            }
                        }
                    }

                    // Raw JSON Inspector
                    item {
                        val event = WebhookSimulator.buildIncomingWebhookPayload(
                            customerPhone, customerName, incomingMessageText, settings.phoneId
                        )
                        Text(
                            text = "Meta Graph API Webhook JSON Payload:",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CodeBgDark)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = event.incomingJson,
                                color = WhatsAppLightGreen,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                2 -> {
                    // Webhook Logs History
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Live Webhook Activity Logs",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            IconButton(onClick = {
                                viewModel.clearWebhookLogs()
                                Toast.makeText(context, "Cleared logs", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(imageVector = Icons.Default.ClearAll, contentDescription = "Clear Logs")
                            }
                        }
                    }

                    if (webhookLogs.isEmpty()) {
                        item {
                            Text(
                                text = "No webhook logs yet. Send a test message or verify handshake above.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    items(webhookLogs, key = { it.id }) { log ->
                        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                        val formattedTime = timeFormat.format(Date(log.timestamp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (log.method == "GET") WhatsAppTeal else WhatsAppDarkTeal)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = log.method,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }

                                        Text(
                                            text = "HTTP ${log.status}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (log.status == 200) SuccessGreen else EscalationRed
                                        )
                                    }

                                    Text(
                                        text = "$formattedTime • ${log.executionTimeMs}ms",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = log.endpoint,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                if (log.customerPhone != null) {
                                    Text(
                                        text = "From Customer: ${log.customerPhone}",
                                        fontSize = 11.sp,
                                        color = WhatsAppTeal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
