package com.nikoladrljaca.getreminded.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nikoladrljaca.getreminded.viewmodel.Reminder

@Database(entities = arrayOf(Reminder::class), version = 1, exportSchema = false)
public abstract class ReminderDatabase: RoomDatabase() {
    abstract fun reminderDao(): ReminderDao

    companion object{
        @Volatile
        private var INSTANCE: ReminderDatabase? = null

        fun getDatabase(context: Context): ReminderDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReminderDatabase::class.java,
                    "reminder_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}