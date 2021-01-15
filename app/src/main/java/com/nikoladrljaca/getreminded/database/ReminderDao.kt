package com.nikoladrljaca.getreminded.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nikoladrljaca.getreminded.viewmodel.DeletedReminder
import com.nikoladrljaca.getreminded.viewmodel.Reminder
import kotlinx.coroutines.flow.Flow

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
    fun getAllRemindersFlow(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminder_table WHERE id=:id")
    suspend fun getReminder(id: Int): Reminder

    //methods to access deleted reminders
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deletedReminder: DeletedReminder)

    @Query("DELETE FROM deleted_reminder_table")
    suspend fun deleteAllDeletedReminders()

    @Delete
    suspend fun deleteEntry(deletedReminder: DeletedReminder)

    @Query("SELECT * FROM deleted_reminder_table ORDER BY date DESC")
    fun getAllDeletedRemindersFlow(): Flow<List<DeletedReminder>>
}