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