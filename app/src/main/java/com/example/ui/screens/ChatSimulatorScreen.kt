package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.WhatsAppChatBubble
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.LightChatBg
import com.example.ui.theme.WhatsAppDarkTeal
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTeal
import com.example.ui.viewmodel.BotViewModel

@Composable
fun ChatSimulatorScreen(
    viewModel: BotViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isDark = isSystemInDarkTheme()

    val settings by viewModel.settings.collectAsState()
    val messages by viewModel.currentSessionMessages.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()
    val activeSessionId by viewModel.activeSessionId.collectAsState()
    val activeCustomerName by viewModel.activeCustomerName.collectAsState()
    val lastReplyResult by viewModel.lastReplyResult.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var sendViaRealApi by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val customerPresets = listOf(
        Pair("+92 300 9876543", "Ahmed Khan (Karachi)"),
        Pair("+92 321 4567890", "Fatima Bibi (Lahore)"),
        Pair("+92 333 1122334", "Bilal Tariq (Islamabad)"),
        Pair("+1 415 555 0199", "John Doe (Overseas)")
    )

    val quickQuestions = listOf(
        "Bhai biryani ka rate kya hai?",
        "Chicken karahi full kitne ki hai?",
        "Delivery timing kya hai?",
        "EasyPaisa accept hota hai?",
        "Shop ka address kahan hai?",
        "Deal 1 mein kya kya hai?",
        "Human agent se baat karni hai"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) DarkBg else LightChatBg)
            .imePadding()
            .navigationBarsPadding()
            .testTag("chat_simulator_screen")
    ) {
        // WhatsApp Custom Chat Header
        Surface(
            color = if (isDark) DarkSurface else WhatsAppDarkTeal,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Bot Avatar
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(WhatsAppLightGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "Bot Avatar",
                            tint = if (isDark) WhatsAppLightGreen else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = settings.businessName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = WhatsAppLightGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = if (isThinking) "typing..." else "Simulating with $activeCustomerName ($activeSessionId)",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isThinking) WhatsAppLightGreen else Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        viewModel.clearCurrentChat()
                        Toast.makeText(context, "Chat history cleared", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Chat",
                            tint = Color.White
                        )
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            Text(
                                text = "Switch Customer Profile",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                            customerPresets.forEach { (phone, name) ->
                                DropdownMenuItem(
                                    text = { Text("$name\n$phone", fontSize = 13.sp) },
                                    onClick = {
                                        viewModel.switchCustomer(phone, name)
                                        showMenu = false
                                        Toast.makeText(context, "Switched to $name", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // WhatsApp Chat Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // WhatsApp Encryption Notice
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isDark) Color(0xFF182229) else Color(0xFFFFEECD)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🔒 WhatsApp AI Sandbox: Messages answered in Roman Urdu / English according to ${settings.businessName} FAQs.",
                            fontSize = 11.sp,
                            color = if (isDark) Color(0xFFFFD279) else Color(0xFF54656F),
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            items(messages, key = { it.id }) { msg ->
                val isBot = msg.sender == "BOT"
                WhatsAppChatBubble(
                    text = msg.text,
                    timestamp = msg.timestamp,
                    isBot = isBot,
                    isEscalated = msg.isEscalated,
                    languageBadge = if (isBot) msg.detectedLanguage else null,
                    isDarkTheme = isDark,
                    onLongClick = {
                        clipboardManager.setText(AnnotatedString(msg.text))
                        Toast.makeText(context, "Copied message to clipboard", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // Real-time typing bubble
            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0xFF005C4B) else Color(0xFFE7FFDB))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = WhatsAppTeal
                                )
                                Text(
                                    text = "AI assistant soch raha hai...",
                                    fontSize = 12.sp,
                                    color = if (isDark) Color(0xFFE9EDEF) else Color(0xFF111B21)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Suggestion Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(quickQuestions) { q ->
                SuggestionChip(
                    onClick = {
                        viewModel.sendCustomerMessage(q, sendViaRealApi)
                    },
                    label = { Text(q, fontSize = 11.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (isDark) DarkSurface else Color.White
                    )
                )
            }
        }

        // WhatsApp Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                placeholder = {
                    Text(
                        "Roman Urdu ya English mein sawal likhein...",
                        fontSize = 13.sp
                    )
                },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = if (isDark) DarkSurface else Color.White,
                    unfocusedContainerColor = if (isDark) DarkSurface else Color.White,
                    focusedBorderColor = WhatsAppTeal,
                    unfocusedBorderColor = Color.Transparent
                ),
                maxLines = 3
            )

            FloatingActionButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        val text = inputText
                        inputText = ""
                        viewModel.sendCustomerMessage(text, sendViaRealApi)
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("send_message_button"),
                shape = CircleShape,
                containerColor = WhatsAppTeal,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
