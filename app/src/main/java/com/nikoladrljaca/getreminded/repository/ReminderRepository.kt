package com.nikoladrljaca.getreminded.repository

import androidx.lifecycle.LiveData
import com.nikoladrljaca.getreminded.database.ReminderDao
import com.nikoladrljaca.getreminded.viewmodel.Reminder

class ReminderRepository(private val reminderDao: ReminderDao) {
    val allReminders: LiveData<List<Reminder>> = reminderDao.getAllReminders()

    suspend fun insert(reminder: Reminder){
        reminderDao.insert(reminder)
    }

    suspend fun deleteEntry(reminder: Reminder){
        reminderDao.deleteEntry(reminder)
    }

    suspend fun deleteAll() {
        reminderDao.deleteAll()
    }

    suspend fun updateEntry(reminder: Reminder) {
        reminderDao.updateEntry(reminder)
    }
}