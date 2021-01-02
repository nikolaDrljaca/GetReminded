package com.nikoladrljaca.getreminded.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.nikoladrljaca.getreminded.database.ReminderDatabase
import com.nikoladrljaca.getreminded.repository.ReminderRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ReminderRepository
    var allReminders: LiveData<List<Reminder>>

    private val _displayReminder = MutableLiveData<Reminder>()
    val displayReminder: LiveData<Reminder> get() = _displayReminder

    init {
        val reminderDao = ReminderDatabase.getDatabase(application).reminderDao()
        repository = ReminderRepository(reminderDao)
        allReminders = repository.allReminders
    }

    fun setDisplayReminder(reminderId: Int) {
        viewModelScope.launch {
            _displayReminder.value = repository.getReminder(reminderId)
        }
    }

    fun insert(title: String,
               exists: Boolean,
               date: Long,
               note: String,
               id: Int) = viewModelScope.launch {
        if (title.isNotEmpty()) {
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
                repository.updateEntry(reminder)
            }
        } catch (e: Exception) {

        }
    }

    private fun insert(reminder: Reminder) {
        try {
            viewModelScope.launch {
                repository.insert(reminder)
            }
        } catch (e: Exception) {

        }
    }

    fun deleteAll() {
        try {
            viewModelScope.launch {
                repository.deleteAll()
            }
        } catch (e: Exception) {

        }
    }

    private val reminderEventChannel = Channel<MainEvents>()
    val reminderEvents = reminderEventChannel.receiveAsFlow()

    fun onReminderSwiped(reminder: Reminder) = viewModelScope.launch {
        repository.deleteEntry(reminder)
        reminderEventChannel.send(MainEvents.ShowUndoReminderDeleteMessage(reminder))
    }

    fun onUndoDeleteClick(reminder: Reminder) = viewModelScope.launch {
        repository.insert(reminder)
    }

    sealed class MainEvents {
        data class ShowUndoReminderDeleteMessage(val reminder: Reminder) : MainEvents()
    }
}