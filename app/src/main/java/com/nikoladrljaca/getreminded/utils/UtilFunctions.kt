package com.nikoladrljaca.getreminded.utils

import com.nikoladrljaca.getreminded.viewmodel.DeletedReminder
import com.nikoladrljaca.getreminded.viewmodel.Reminder
import java.text.SimpleDateFormat
import java.util.*

fun dateFromEpoch(epoch: Long): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.US)
    val date = Date(epoch)
    return sdf.format(date)
}

fun mapFromReminderToDeletedReminder(reminder: Reminder): DeletedReminder {
    return DeletedReminder(
        id = reminder.id!!,
        title = reminder.title,
        note = reminder.note,
        date = reminder.date
    )
}

fun mapFromDeletedReminderToReminder(deleted: DeletedReminder): Reminder {
    return Reminder(
        title = deleted.title,
        note = deleted.note,
        date = deleted.date
    )
}
