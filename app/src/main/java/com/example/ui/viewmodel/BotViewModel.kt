package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BotSettingsEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.FaqEntity
import com.example.data.local.WebhookLogEntity
import com.example.data.repository.BotRepository
import com.example.domain.BotReplyResult
import com.example.domain.VerificationResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class BotViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BotRepository(AppDatabase.getInstance(application))

    val settings: StateFlow<BotSettingsEntity> = repository.settingsFlow
        .flatMapLatest { flowOfSettings ->
            MutableStateFlow(flowOfSettings ?: BotSettingsEntity())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BotSettingsEntity()
        )

    val faqs: StateFlow<List<FaqEntity>> = repository.faqsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val webhookLogs: StateFlow<List<WebhookLogEntity>> = repository.webhookLogsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalMessagesCount: StateFlow<Int> = repository.totalMessagesCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val escalationsCount: StateFlow<Int> = repository.escalationsCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val escalations: StateFlow<List<ChatMessageEntity>> = repository.escalatedMessagesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _activeSessionId = MutableStateFlow("+92 300 9876543")
    val activeSessionId: StateFlow<String> = _activeSessionId.asStateFlow()

    private val _activeCustomerName = MutableStateFlow("Ahmed Khan")
    val activeCustomerName: StateFlow<String> = _activeCustomerName.asStateFlow()

    val currentSessionMessages: StateFlow<List<ChatMessageEntity>> = _activeSessionId
        .flatMapLatest { sessionId ->
            repository.getChatMessages(sessionId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    private val _lastReplyResult = MutableStateFlow<BotReplyResult?>(null)
    val lastReplyResult: StateFlow<BotReplyResult?> = _lastReplyResult.asStateFlow()

    private val _lastVerificationResult = MutableStateFlow<VerificationResult?>(null)
    val lastVerificationResult: StateFlow<VerificationResult?> = _lastVerificationResult.asStateFlow()

    fun switchCustomer(phone: String, name: String) {
        _activeSessionId.value = phone
        _activeCustomerName.value = name
    }

    fun sendCustomerMessage(text: String, sendRealApi: Boolean = false) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _isThinking.value = true
            try {
                val result = repository.processCustomerMessage(
                    sessionId = _activeSessionId.value,
                    customerName = _activeCustomerName.value,
                    messageText = text.trim(),
                    sendRealApiIfConfigured = sendRealApi
                )
                _lastReplyResult.value = result
            } finally {
                _isThinking.value = false
            }
        }
    }

    fun updateSettings(newSettings: BotSettingsEntity) {
        viewModelScope.launch {
            repository.updateSettings(newSettings)
        }
    }

    fun saveFaq(faq: FaqEntity) {
        viewModelScope.launch {
            if (faq.id == 0) {
                repository.addFaq(faq)
            } else {
                repository.updateFaq(faq)
            }
        }
    }

    fun deleteFaq(faq: FaqEntity) {
        viewModelScope.launch {
            repository.deleteFaq(faq)
        }
    }

    fun clearCurrentChat() {
        viewModelScope.launch {
            repository.clearChatSession(_activeSessionId.value)
        }
    }

    fun resolveEscalation(messageId: Long) {
        viewModelScope.launch {
            repository.resolveEscalation(messageId)
        }
    }

    fun clearWebhookLogs() {
        viewModelScope.launch {
            repository.clearWebhookLogs()
        }
    }

    fun testWebhookVerification(token: String, challenge: String) {
        viewModelScope.launch {
            val result = repository.simulateVerification(token, challenge)
            _lastVerificationResult.value = result
        }
    }
}
