package com.nikoladrljaca.getreminded.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.nikoladrljaca.getreminded.database.ReminderDatabase
import com.nikoladrljaca.getreminded.utils.mapFromReminderToDeletedReminder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val reminderDao = ReminderDatabase.getDatabase(application, viewModelScope).reminderDao()
    val allReminders: LiveData<List<Reminder>> = reminderDao.getAllRemindersFlow().asLiveData()

    private val _displayReminder = MutableLiveData<Reminder>()
    val displayReminder: LiveData<Reminder> get() = _displayReminder

    fun setDisplayReminder(reminderId: Int) {
        viewModelScope.launch {
            _displayReminder.value = reminderDao.getReminder(reminderId)
        }
    }

    fun insert(
        title: String,
        exists: Boolean,
        date: Long,
        note: String,
        id: Int
    ) = viewModelScope.launch {
        if (title.isEmpty() && note.isEmpty()) {
            delay(300) //this delay causes the snackbar to appear properly above the FAB
            reminderEventChannel.send(MainEvents.ShowReminderDiscardedMessage)
        } else {
            val reminder = Reminder(title, note, date)
            if (exists) {
                reminder.id = id
                updateEntry(reminder)
            } else insert(reminder)
        }
    }

    private fun updateEntry(reminder: Reminder) {
        try {
            viewModelScope.launch {
                reminderDao.updateEntry(reminder)
            }
        } catch (e: Exception) {

        }
    }

    private fun insert(reminder: Reminder) {
        try {
            viewModelScope.launch {
                reminderDao.insert(reminder)
            }
        } catch (e: Exception) {

        }
    }

    fun deleteAll() {
        try {
            viewModelScope.launch {
                allReminders.value!!.forEach { reminder ->
                    reminderDao.insert(mapFromReminderToDeletedReminder(reminder))
                }
                reminderDao.deleteAll()
            }
        } catch (e: Exception) {

        }
    }

    private val reminderEventChannel = Channel<MainEvents>()
    val reminderEvents = reminderEventChannel.receiveAsFlow()

    fun onReminderSwiped(reminder: Reminder) = viewModelScope.launch {
        reminderDao.insert(mapFromReminderToDeletedReminder(reminder))
        reminderDao.deleteEntry(reminder)
        reminderEventChannel.send(MainEvents.ShowUndoReminderDeleteMessage(reminder))
    }

    fun onUndoDeleteClick(reminder: Reminder, deletedReminder: DeletedReminder) = viewModelScope.launch {
        reminderDao.deleteEntry(deletedReminder)
        reminderDao.insert(reminder)
    }

    sealed class MainEvents {
        data class ShowUndoReminderDeleteMessage(val reminder: Reminder) : MainEvents() {
            val deletedReminder = mapFromReminderToDeletedReminder(reminder)
        }
        object ShowReminderDiscardedMessage : MainEvents()
    }
}