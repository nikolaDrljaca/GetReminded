package com.nikoladrljaca.getreminded.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nikoladrljaca.getreminded.database.ReminderDatabase
import com.nikoladrljaca.getreminded.utils.mapFromDeletedReminderToReminder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DeletedRemindersViewModel(application: Application) : AndroidViewModel(application) {
    private val reminderDao = ReminderDatabase.getDatabase(application, viewModelScope).reminderDao()
    val allDeletedReminders: LiveData<List<DeletedReminder>> = reminderDao.getAllDeletedRemindersFlow().asLiveData()


    fun deleteAllReminders() = viewModelScope.launch {
        reminderDao.deleteAllDeletedReminders()
    }

    fun onRestoreAllClicked() = viewModelScope.launch {
        allDeletedReminders.value!!.forEach { deletedReminder ->
            reminderDao.insert(mapFromDeletedReminderToReminder(deletedReminder))
        }
        deleteAllReminders()
        //fire off event to trigger a snackbar toast in the fragment
        deletedRemindersChannel.send(DeletedRemindersEvents.ShowAllRestoredMessage)
    }

    fun onDeleteReminderClicked(position: Int) = viewModelScope.launch {
        allDeletedReminders.value?.let { list ->
            reminderDao.deleteEntry(list[position])
        }
    }

    fun onRestoreReminderClicked(position: Int) = viewModelScope.launch {
        allDeletedReminders.value?.let { list ->
            val current = list[position]
            reminderDao.insert(mapFromDeletedReminderToReminder(current))
            reminderDao.deleteEntry(current)
        }
    }

    private val deletedRemindersChannel = Channel<DeletedRemindersEvents>()
    val events = deletedRemindersChannel.receiveAsFlow()

    sealed class DeletedRemindersEvents {
        object ShowAllRestoredMessage: DeletedRemindersEvents()
    }
}