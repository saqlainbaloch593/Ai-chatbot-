package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Webhook
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    DASHBOARD("dashboard", "Overview", Icons.Default.Dashboard),
    CHAT_SIMULATOR("chat_simulator", "WA Chat", Icons.AutoMirrored.Filled.Chat),
    BOT_CONFIG("bot_config", "Bot & Prompt", Icons.Default.Settings),
    FAQ_MANAGER("faq_manager", "Knowledge Base", Icons.AutoMirrored.Filled.HelpOutline),
    WEBHOOK_TESTER("webhook_tester", "Webhook Tester", Icons.Default.Webhook),
    CODE_EXPORTER("code_exporter", "Server Code", Icons.Default.Code),
    ESCALATIONS("escalations", "Escalations", Icons.Default.NotificationsActive)
}
