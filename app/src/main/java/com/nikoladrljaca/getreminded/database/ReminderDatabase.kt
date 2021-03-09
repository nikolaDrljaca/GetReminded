package com.nikoladrljaca.getreminded.database

import android.content.Context

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase

import com.nikoladrljaca.getreminded.viewmodel.DeletedReminder
import com.nikoladrljaca.getreminded.viewmodel.Reminder
import com.nikoladrljaca.getreminded.viewmodel.welcomeReminder
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.launch


@Database(entities = arrayOf(Reminder::class, DeletedReminder::class), version = 4, exportSchema = false)
public abstract class ReminderDatabase: RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object{
        @Volatile
        private var INSTANCE: ReminderDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ReminderDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReminderDatabase::class.java,
                    "reminder_database"
                )
                    .addCallback(ReminderDatabaseCallback(scope))
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class ReminderDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val dao = database.reminderDao()
                    dao.insert(welcomeReminder)
                }
            }
        }
    }
}