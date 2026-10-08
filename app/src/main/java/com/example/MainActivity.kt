package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Screen
import com.example.ui.screens.BotConfigScreen
import com.example.ui.screens.ChatSimulatorScreen
import com.example.ui.screens.CodeExporterScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EscalationsScreen
import com.example.ui.screens.FaqManagerScreen
import com.example.ui.screens.WebhookTesterScreen
import com.example.ui.theme.EscalationAmber
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.WhatsAppDarkTeal
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTeal
import com.example.ui.viewmodel.BotViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BotViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(viewModel: BotViewModel) {
    var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }
    val escalationsCount by viewModel.escalationsCount.collectAsState()

    // Handle back button for secondary screens
    BackHandler(enabled = currentScreen != Screen.DASHBOARD) {
        currentScreen = Screen.DASHBOARD
    }

    val primaryNavScreens = listOf(
        Screen.DASHBOARD,
        Screen.CHAT_SIMULATOR,
        Screen.BOT_CONFIG,
        Screen.FAQ_MANAGER,
        Screen.WEBHOOK_TESTER
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen != Screen.CHAT_SIMULATOR) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = currentScreen.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                    },
                    navigationIcon = {
                        if (currentScreen != Screen.DASHBOARD) {
                            IconButton(onClick = { currentScreen = Screen.DASHBOARD }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back to Overview"
                                )
                            }
                        }
                    },
                    actions = {
                        // Quick shortcut to Code Exporter
                        IconButton(
                            onClick = { currentScreen = Screen.CODE_EXPORTER },
                            modifier = Modifier.testTag("appbar_code_action")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "Python Server Code",
                                tint = if (currentScreen == Screen.CODE_EXPORTER) WhatsAppLightGreen else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Escalations button with badge
                        IconButton(
                            onClick = { currentScreen = Screen.ESCALATIONS },
                            modifier = Modifier.testTag("appbar_escalations_action")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (escalationsCount > 0) {
                                        Badge(containerColor = EscalationAmber) {
                                            Text(escalationsCount.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "Human Escalations Desk"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                primaryNavScreens.forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = WhatsAppDarkTeal,
                            selectedTextColor = WhatsAppDarkTeal,
                            indicatorColor = WhatsAppLightGreen.copy(alpha = 0.25f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigate = { currentScreen = it }
                )
                Screen.CHAT_SIMULATOR -> ChatSimulatorScreen(
                    viewModel = viewModel
                )
                Screen.BOT_CONFIG -> BotConfigScreen(
                    viewModel = viewModel
                )
                Screen.FAQ_MANAGER -> FaqManagerScreen(
                    viewModel = viewModel
                )
                Screen.WEBHOOK_TESTER -> WebhookTesterScreen(
                    viewModel = viewModel
                )
                Screen.CODE_EXPORTER -> CodeExporterScreen(
                    viewModel = viewModel
                )
                Screen.ESCALATIONS -> EscalationsScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
