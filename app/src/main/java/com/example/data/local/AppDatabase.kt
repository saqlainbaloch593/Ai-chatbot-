package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BotSettingsEntity::class,
        FaqEntity::class,
        ChatMessageEntity::class,
        WebhookLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun botSettingsDao(): BotSettingsDao
    abstract fun faqDao(): FaqDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun webhookLogDao(): WebhookLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wabot_studio.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                prepopulateDatabase(database)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun prepopulateDatabase(db: AppDatabase) {
            val settingsDao = db.botSettingsDao()
            val faqDao = db.faqDao()

            if (settingsDao.getSettings() == null) {
                settingsDao.insertOrUpdate(BotSettingsEntity())
            }

            val defaultFaqs = listOf(
                FaqEntity(
                    question = "Biryani aur Karahi ki prices kya hain?",
                    answer = "Chicken Biryani Single: Rs. 380, Double: Rs. 550. Beef Biryani: Rs. 480. Chicken Karahi Full: Rs. 1,450 / Half: Rs. 850. Raita aur Salad har biryani ke saath complimentary hai!",
                    category = "Menu & Pricing",
                    keywords = "price, prices, biryani, karahi, chicken, rate, menu, khana"
                ),
                FaqEntity(
                    question = "Aapke restaurant ke timings aur opening hours kya hain?",
                    answer = "Hamara restaurant rozana dopehar 12:00 PM se raat 1:00 AM tak khula rehta hai. Friday ko jumma prayer ke baad 1:30 PM open hota hai.",
                    category = "Timings",
                    keywords = "timing, timings, open, close, time, kab, hours, schedule"
                ),
                FaqEntity(
                    question = "Delivery charges kitne hain aur kitne time mein pohnchega?",
                    answer = "DHA aur Clifton mein delivery 35-45 minutes mein deliver hoti hai. Rs. 1000 se zyada ke order par Free Delivery hai, warna flat Rs. 120 delivery charge hai.",
                    category = "Delivery",
                    keywords = "delivery, charges, time, rider, fees, free, ghar, parcel"
                ),
                FaqEntity(
                    question = "Payment kaise kar sakte hain? JazzCash ya EasyPaisa accept hota hai?",
                    answer = "Ji bilkul! Hamare paas Cash on Delivery (COD), JazzCash, EasyPaisa, aur Raast / Direct Bank Transfer accept hota hai.",
                    category = "Payment",
                    keywords = "payment, jazzcash, easypaisa, cod, cash, online, card, raast"
                ),
                FaqEntity(
                    question = "Restaurant ka address kahan hai aur kya dine-in available hai?",
                    answer = "Hamara address: Plot 12-C, Commercial Lane, Clifton Block 4, Karachi. Family hall aur separate AC seating available hai.",
                    category = "Location",
                    keywords = "address, location, shop, restaurant, dine in, kahan, kidhar"
                ),
                FaqEntity(
                    question = "Aaj ke special deals aur family discount packages kya hain?",
                    answer = "Deal 1: 2 Chicken Biryani + 2 Cold Drinks + Raita = Rs. 850. Deal 2: Full Chicken Karahi + 4 Roghni Naan + 1.5L Coke = Rs. 1,750.",
                    category = "Deals",
                    keywords = "deal, deals, discount, offer, package, bachat, family"
                )
            )
            faqDao.insertAll(defaultFaqs)
        }
    }
}
