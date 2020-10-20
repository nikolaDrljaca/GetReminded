package com.nikoladrljaca.getreminded.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.nikoladrljaca.getreminded.database.ReminderDatabase
import com.nikoladrljaca.getreminded.repository.ReminderRepository
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

    fun updateEntry(reminder: Reminder) {
        try {
            viewModelScope.launch {
                repository.updateEntry(reminder)
            }
        } catch (e: Exception) {

        }
    }

    fun insert(reminder: Reminder) {
        try {
            viewModelScope.launch {
                repository.insert(reminder)
            }
        } catch (e: Exception) {

        }
    }

    fun deleteEntry(reminder: Reminder, delete: Boolean) {
        viewModelScope.launch {
            if (delete) repository.deleteEntry(reminder)
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
}