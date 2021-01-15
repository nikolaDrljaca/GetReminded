package com.nikoladrljaca.getreminded.viewmodel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_table")
data class Reminder (
    val title: String,
    val note: String,
    val date: Long
){
    @PrimaryKey(autoGenerate = true) var id: Int? = null
}

@Entity(tableName = "deleted_reminder_table")
data class DeletedReminder (
    @PrimaryKey val id: Int,
    val title: String,
    val note: String,
    val date: Long
)

val welcomeReminder = Reminder(
    title = "Welcome!",
    note = "Here are a few things to get you started:\n\n" +
            "*To delete a reminder, just swipe it to any side!\n\n" +
            "*To save a reminder, just go back, all changes are saved automatically!\n\n" +
            "*You can always change the time using the clock in the bottom corner\n\n" +
            "*The notes are ordered by date\n\n" +
            "*Ignore the date below, this app cannot time-travel. Though it would be cool if it could :D",
    date = 1
)