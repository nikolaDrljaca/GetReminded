package com.nikoladrljaca.getreminded


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.nikoladrljaca.getreminded.utils.AlertReceiver
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var hour = 0
    private var minutes = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkTheme()
        setContentView(R.layout.activity_main)
        checkDarkMode()

        val shared = PreferenceManager.getDefaultSharedPreferences(this)
        val hasSeenDialog = shared.getBoolean(HAS_SEEN_DIALOG, false)
        if (!hasSeenDialog) showStartDialog()
    }

    fun checkDarkMode() {
        val shared = PreferenceManager.getDefaultSharedPreferences(this)
        val darkModeToggle = shared.getBoolean("dark_mode_switch", false)
        if (darkModeToggle) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun checkTheme() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val colorList = prefs.getString("accent_color_list", "null")
        if (colorList == "red") setTheme(R.style.AppThemeRed)
        if (colorList == "green") setTheme(R.style.AppThemeGreen)
        if (colorList == "blue") setTheme(R.style.AppTheme)
        if (colorList == "orange") setTheme(R.style.AppThemeOrange)
        if (colorList == "purple") setTheme(R.style.AppThemePurple)
    }

    private fun showStartDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(getString(R.string.welcome_dialog))
        alertDialogBuilder.setMessage(getString(R.string.welcome_message))
        alertDialogBuilder.setPositiveButton(getString(R.string.alert_dialog_ok)){
            dialog, _ ->
            //display clock dialog and save value to shared prefs
            val shared = PreferenceManager.getDefaultSharedPreferences(this).edit()
            shared.putBoolean(HAS_SEEN_DIALOG, true)
            shared.apply()
            dialog.dismiss()
            showTimePicker()
        }
        //close the app if dialog is canceled or set a default time?
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel)){
            dialog, _ ->
            finish()
        }

        alertDialogBuilder.create().show()
    }

    fun showTimePicker() {
        val timeBuilder = MaterialTimePicker.newInstance()
        timeBuilder.setListener {
            hour = it.hour
            minutes = it.minute

            //here you want to set the time for AlarmManager
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minutes)
            calendar.set(Calendar.SECOND, 0)

            //here start the alarmManager and pass this calendar instance
            startAlarm(calendar)

            //save to sharedPrefs
            val shared = PreferenceManager.getDefaultSharedPreferences(this).edit()
            shared.putLong("reminder_time", calendar.timeInMillis)
            shared.apply()

            Snackbar.make(fab_create_new_reminder, getString(R.string.time_saved), Snackbar.LENGTH_SHORT)
                .setAnchorView(fab_create_new_reminder)
                .show()
        }
        timeBuilder.show(supportFragmentManager, "tag")
    }

    private fun startAlarm(calendar: Calendar) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0)

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, //wakes up the device
            calendar.timeInMillis, //alarm fires off at this time
            AlarmManager.INTERVAL_DAY, //this is the repeating interval
            pendingIntent
        )
    }

    companion object {
        private const val HAS_SEEN_DIALOG = "has_seen_dialog"
    }
}