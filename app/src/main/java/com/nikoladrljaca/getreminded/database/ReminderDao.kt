package com.nikoladrljaca.getreminded.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nikoladrljaca.getreminded.viewmodel.Reminder

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(reminder: Reminder)

    @Query("DELETE FROM reminder_table")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteEntry(reminder: Reminder)

    @Update
    suspend fun updateEntry(reminder: Reminder)

    @Query("SELECT * from reminder_table ORDER BY date DESC")
    fun getAllReminders(): LiveData<List<Reminder>>
}