package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.StatsMetricCard
import com.example.ui.components.StatusPill
import com.example.ui.navigation.Screen
import com.example.ui.theme.EscalationAmber
import com.example.ui.theme.WhatsAppDarkTeal
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTeal
import com.example.ui.viewmodel.BotViewModel

@Composable
fun DashboardScreen(
    viewModel: BotViewModel,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val totalMessages by viewModel.totalMessagesCount.collectAsState()
    val escalationsCount by viewModel.escalationsCount.collectAsState()
    val faqs by viewModel.faqs.collectAsState()

    val quickTestPrompts = listOf(
        "Bhai biryani ka rate kya hai?",
        "Opening timings batao",
        "Delivery charges kitne hain?",
        "Main human agent se baat karna chahta hoon",
        "What payment methods do you accept?"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card with Generated Graphic
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_wabot_banner_1791465905927),
                        contentDescription = "WhatsApp AI Bot Hero",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        WhatsAppDarkTeal.copy(alpha = 0.88f)
                                    ),
                                    startY = 60f
                                )
                            )
                    )

                    // Text overlay
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = settings.businessName,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified WhatsApp Bot",
                                tint = WhatsAppLightGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "WhatsApp AI Assistant • Bilingual (Roman Urdu / English)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            StatusPill(
                                label = if (settings.isBotActive) "Live & Answering" else "Paused",
                                isActive = settings.isBotActive
                            )
                            StatusPill(
                                label = "Model: ${settings.modelName}",
                                isActive = true
                            )
                        }
                    }
                }
            }
        }

        // Live Bot Active Switch
        item {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(WhatsAppTeal.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = WhatsAppTeal
                            )
                        }
                        Column {
                            Text(
                                text = "AI Auto-Reply Engine",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = if (settings.isBotActive) "Simulating & receiving WhatsApp webhooks" else "Bot responses paused",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = settings.isBotActive,
                        onCheckedChange = { viewModel.updateSettings(settings.copy(isBotActive = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = WhatsAppLightGreen
                        )
                    )
                }
            }
        }

        // 4 Metric Stats Cards
        item {
            Text(
                text = "Performance & Metrics",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsMetricCard(
                    title = "Total Chats",
                    value = totalMessages.toString(),
                    subtitle = "Logged messages",
                    icon = Icons.Default.Forum,
                    iconTint = WhatsAppTeal,
                    modifier = Modifier.weight(1f)
                )
                StatsMetricCard(
                    title = "Active FAQs",
                    value = faqs.filter { it.isEnabled }.size.toString(),
                    subtitle = "Knowledge items",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    iconTint = WhatsAppLightGreen,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsMetricCard(
                    title = "Human Alerts",
                    value = escalationsCount.toString(),
                    subtitle = "Staff handovers",
                    icon = Icons.Default.WarningAmber,
                    iconTint = EscalationAmber,
                    modifier = Modifier.weight(1f)
                )
                StatsMetricCard(
                    title = "Webhook Handshake",
                    value = "200 OK",
                    subtitle = "Meta Graph API v21.0",
                    icon = Icons.Default.CheckCircle,
                    iconTint = WhatsAppLightGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Test Launcher Chips
        item {
            Text(
                text = "Instant Bilingual Test Queries",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Tap any question to launch the WhatsApp Simulator with immediate AI answer:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickTestPrompts) { prompt ->
                    SuggestionChip(
                        onClick = {
                            viewModel.sendCustomerMessage(prompt)
                            onNavigate(Screen.CHAT_SIMULATOR)
                        },
                        label = { Text(prompt, fontSize = 12.sp) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = WhatsAppTeal,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }
        }

        // Quick Navigation Hub
        item {
            Text(
                text = "Studio Workspaces",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    WorkspaceNavItem(
                        title = "WhatsApp Chat Simulator",
                        description = "Chat in Roman Urdu/English with live AI replies & delivery ticks",
                        icon = Icons.Default.Forum,
                        iconTint = WhatsAppTeal,
                        onClick = { onNavigate(Screen.CHAT_SIMULATOR) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    WorkspaceNavItem(
                        title = "Bot Prompt & Credentials",
                        description = "Edit Roman Urdu system prompt, Meta WA_TOKEN & PHONE_ID",
                        icon = Icons.Default.Settings,
                        iconTint = WhatsAppDarkTeal,
                        onClick = { onNavigate(Screen.BOT_CONFIG) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    WorkspaceNavItem(
                        title = "Knowledge Base & FAQs",
                        description = "Manage business catalog, item prices, timings & store policies",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        iconTint = WhatsAppLightGreen,
                        onClick = { onNavigate(Screen.FAQ_MANAGER) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    WorkspaceNavItem(
                        title = "Meta Webhook Tester",
                        description = "Simulate GET verification handshake & POST WhatsApp payloads",
                        icon = Icons.Default.ElectricBolt,
                        iconTint = WhatsAppTeal,
                        onClick = { onNavigate(Screen.WEBHOOK_TESTER) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    WorkspaceNavItem(
                        title = "Python Flask Server Code",
                        description = "1-tap view & copy ready-to-deploy backend server code",
                        icon = Icons.Default.Terminal,
                        iconTint = Color(0xFF673AB7),
                        onClick = { onNavigate(Screen.CODE_EXPORTER) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    WorkspaceNavItem(
                        title = "Human Escalation Desk",
                        description = "Review queries where bot asked for human staff takeover",
                        icon = Icons.Default.SupportAgent,
                        iconTint = EscalationAmber,
                        onClick = { onNavigate(Screen.ESCALATIONS) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkspaceNavItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
    }
}
