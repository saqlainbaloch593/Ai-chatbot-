package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BotSettingsDao {
    @Query("SELECT * FROM bot_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<BotSettingsEntity?>

    @Query("SELECT * FROM bot_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): BotSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: BotSettingsEntity)
}

@Dao
interface FaqDao {
    @Query("SELECT * FROM faqs ORDER BY category ASC, id ASC")
    fun getAllFaqs(): Flow<List<FaqEntity>>

    @Query("SELECT * FROM faqs WHERE isEnabled = 1")
    suspend fun getEnabledFaqs(): List<FaqEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaq(faq: FaqEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(faqs: List<FaqEntity>)

    @Update
    suspend fun updateFaq(faq: FaqEntity)

    @Delete
    suspend fun deleteFaq(faq: FaqEntity)

    @Query("DELETE FROM faqs WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT DISTINCT sessionId FROM chat_messages ORDER BY id DESC")
    fun getAllSessions(): Flow<List<String>>

    @Query("SELECT * FROM chat_messages WHERE isEscalated = 1 ORDER BY timestamp DESC")
    fun getEscalatedMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT COUNT(*) FROM chat_messages")
    fun getTotalMessageCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM chat_messages WHERE isEscalated = 1")
    fun getEscalationCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun clearSession(sessionId: String)

    @Query("UPDATE chat_messages SET isEscalated = 0 WHERE id = :id")
    suspend fun resolveEscalation(id: Long)
}

@Dao
interface WebhookLogDao {
    @Query("SELECT * FROM webhook_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllLogs(): Flow<List<WebhookLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: WebhookLogEntity): Long

    @Query("DELETE FROM webhook_logs")
    suspend fun clearLogs()
}
