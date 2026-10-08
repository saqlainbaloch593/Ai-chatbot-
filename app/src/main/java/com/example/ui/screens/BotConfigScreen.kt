package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BotSettingsEntity
import com.example.ui.theme.WhatsAppDarkTeal
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTeal
import com.example.ui.viewmodel.BotViewModel

@Composable
fun BotConfigScreen(
    viewModel: BotViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()

    var businessName by remember(settings) { mutableStateOf(settings.businessName) }
    var businessIndustry by remember(settings) { mutableStateOf(settings.businessIndustry) }
    var businessPhone by remember(settings) { mutableStateOf(settings.businessPhone) }
    var businessHours by remember(settings) { mutableStateOf(settings.businessHours) }
    var businessAddress by remember(settings) { mutableStateOf(settings.businessAddress) }
    var currency by remember(settings) { mutableStateOf(settings.currency) }

    var verifyToken by remember(settings) { mutableStateOf(settings.verifyToken) }
    var waToken by remember(settings) { mutableStateOf(settings.waToken) }
    var phoneId by remember(settings) { mutableStateOf(settings.phoneId) }
    var apiKeyOverride by remember(settings) { mutableStateOf(settings.geminiApiKeyOverride) }

    var escalationMsg by remember(settings) { mutableStateOf(settings.humanEscalationMessage) }
    var autoEscalate by remember(settings) { mutableStateOf(settings.autoEscalateUnsure) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("bot_config_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Header
        item {
            Text(
                text = "Bot & Prompt Studio",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Configure your WhatsApp Business profile, bilingual system prompt, and Meta Cloud API keys.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section 1: Business Identity
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            tint = WhatsAppTeal
                        )
                        Text(
                            text = "Business Profile",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("Business Name") },
                        modifier = Modifier.fillMaxWidth().testTag("config_biz_name")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = businessIndustry,
                            onValueChange = { businessIndustry = it },
                            label = { Text("Category / Industry") },
                            modifier = Modifier.weight(1.5f)
                        )
                        OutlinedTextField(
                            value = currency,
                            onValueChange = { currency = it },
                            label = { Text("Currency") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = businessPhone,
                        onValueChange = { businessPhone = it },
                        label = { Text("WhatsApp Phone / Support Hotline") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = businessHours,
                        onValueChange = { businessHours = it },
                        label = { Text("Business Timings (Opening Hours)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = businessAddress,
                        onValueChange = { businessAddress = it },
                        label = { Text("Store / Branch Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Section 2: Meta WhatsApp Cloud API Credentials
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = WhatsAppDarkTeal
                        )
                        Text(
                            text = "WhatsApp Cloud API Credentials",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Text(
                        text = "Values correspond to your Meta Developers App and Python webhook server variables:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = verifyToken,
                        onValueChange = { verifyToken = it },
                        label = { Text("VERIFY_TOKEN (Webhook Secret)") },
                        modifier = Modifier.fillMaxWidth().testTag("config_verify_token")
                    )

                    OutlinedTextField(
                        value = phoneId,
                        onValueChange = { phoneId = it },
                        label = { Text("PHONE_ID (WhatsApp Phone Number ID)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = waToken,
                        onValueChange = { waToken = it },
                        label = { Text("WA_TOKEN (Meta Graph API Access Token)") },
                        placeholder = { Text("EAA...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Section 3: AI Prompting & Escalation Rules
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = WhatsAppLightGreen
                        )
                        Text(
                            text = "AI System Prompt & Handover Rules",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Text(
                        text = "Preset Industry Templates:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        SuggestionChip(
                            onClick = {
                                businessName = "Karachi Spice & Grill"
                                businessIndustry = "Food & Restaurant"
                                businessHours = "12:00 PM - 1:00 AM (Daily)"
                                currency = "PKR"
                            },
                            label = { Text("Restaurant", fontSize = 11.sp) }
                        )
                        SuggestionChip(
                            onClick = {
                                businessName = "Zahra Haute Couture"
                                businessIndustry = "Fashion Boutique"
                                businessHours = "11:00 AM - 9:00 PM"
                                currency = "PKR"
                            },
                            label = { Text("Boutique", fontSize = 11.sp) }
                        )
                        SuggestionChip(
                            onClick = {
                                businessName = "TechHub Pakistan"
                                businessIndustry = "Electronics & Gadgets"
                                businessHours = "10:00 AM - 10:00 PM"
                                currency = "PKR"
                            },
                            label = { Text("Tech Store", fontSize = 11.sp) }
                        )
                    }

                    OutlinedTextField(
                        value = escalationMsg,
                        onValueChange = { escalationMsg = it },
                        label = { Text("Human Escalation Fallback Message") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = apiKeyOverride,
                        onValueChange = { apiKeyOverride = it },
                        label = { Text("Gemini API Key (Optional override)") },
                        placeholder = { Text("Uses injected Secrets / Local Engine if empty") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto-Escalate Unhandled Queries",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Trigger human handover when question is not in FAQs",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = autoEscalate,
                            onCheckedChange = { autoEscalate = it }
                        )
                    }
                }
            }
        }

        // Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        businessName = "Karachi Spice & Grill"
                        businessIndustry = "Food & Restaurant"
                        businessPhone = "+92 300 1234567"
                        businessHours = "12:00 PM - 1:00 AM (Daily)"
                        businessAddress = "Clifton Block 4, Karachi, Pakistan"
                        currency = "PKR"
                        verifyToken = "my_wabot_verify_token_786"
                        escalationMsg = "Main aapko human agent se connect karta hoon."
                        Toast.makeText(context, "Reset to defaults", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }

                Button(
                    onClick = {
                        val updated = settings.copy(
                            businessName = businessName.trim(),
                            businessIndustry = businessIndustry.trim(),
                            businessPhone = businessPhone.trim(),
                            businessHours = businessHours.trim(),
                            businessAddress = businessAddress.trim(),
                            currency = currency.trim(),
                            verifyToken = verifyToken.trim(),
                            waToken = waToken.trim(),
                            phoneId = phoneId.trim(),
                            geminiApiKeyOverride = apiKeyOverride.trim(),
                            humanEscalationMessage = escalationMsg.trim(),
                            autoEscalateUnsure = autoEscalate
                        )
                        viewModel.updateSettings(updated)
                        Toast.makeText(context, "Settings saved successfully!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("save_settings_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppTeal)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Settings")
                }
            }
        }
    }
}
