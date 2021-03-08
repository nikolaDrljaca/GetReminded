package com.nikoladrljaca.getreminded.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.preference.*
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nikoladrljaca.getreminded.MainActivity
import com.nikoladrljaca.getreminded.R
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab_create_new_reminder)
        val bottomAppBar = requireActivity().findViewById<BottomAppBar>(R.id.bottomAppBar)

        fab.visibility = View.INVISIBLE
        bottomAppBar.visibility = View.INVISIBLE
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val accentColorPref: ListPreference? = findPreference("accent_color_list")
        val darkModePref: SwitchPreferenceCompat? = findPreference("dark_mode_switch")
        //preferences
        val rateThisAppPref: Preference? = findPreference("rate_this_app")
        val sharePref: Preference? = findPreference("share")
        val donate: Preference? = findPreference("donate")
        val openSourceLicenses: Preference? = findPreference("open_source_licenses")
        val currentReminderTime: Preference? = findPreference("reminder_time")
        //val youtube: Preference? = findPreference("youtube")
        //val twitter: Preference? = findPreference("twitter")
        val bugReport: Preference? = findPreference("bug_report")
        //val instagram: Preference? = findPreference("")
        //implement the settings here?
        val muteNotify: Preference? = findPreference("mute_notify")

        val shared = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = shared.getLong("reminder_time", 0)
        val sdf = SimpleDateFormat("HH:mm")
        currentReminderTime?.summary = sdf.format(calendar.time)

        currentReminderTime?.setOnPreferenceClickListener {
            Toast.makeText(
                requireContext(),
                requireActivity().getString(R.string.time_reminder),
                Toast.LENGTH_SHORT
            ).show()
            true
        }

        accentColorPref?.setOnPreferenceChangeListener { _, _ ->
            (activity as MainActivity).recreate()
            true
        }

        darkModePref?.setOnPreferenceClickListener {
            (activity as MainActivity).checkDarkMode()
            true
        }

        rateThisAppPref?.setOnPreferenceClickListener {
            val page = Uri.parse("https://play.google.com/store/apps/details?id=com.nikoladrljaca.getreminded")
            val intent = Intent(Intent.ACTION_VIEW, page)
            startActivity(intent)
            true
        }

        sharePref?.setOnPreferenceClickListener {
            val shareData = getString(R.string.share_my_app)
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareData)
                type = "text/plain"
            }
            val intent = Intent.createChooser(shareIntent, null)
            startActivity(intent)
            true
        }

        bugReport?.setOnPreferenceClickListener {
            val addresses = arrayOf("devnikoladr@gmail.com")
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, addresses)
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.bug_report))
            }
            startActivity(intent)
            true
        }

        donate?.setOnPreferenceClickListener {
            Toast.makeText(requireContext(), "Coming soon!", Toast.LENGTH_SHORT).show()
            true
        }

        openSourceLicenses?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_openSourceFragment)
            true
        }

//        youtube?.setOnPreferenceClickListener {
//            val page = Uri.parse(getString(R.string.youtube_link))
//            val intent = Intent(Intent.ACTION_VIEW, page)
//            startActivity(intent)
//            true
//        }
//
//        twitter?.setOnPreferenceClickListener {
//            Toast.makeText(requireContext(), "Coming soon!", Toast.LENGTH_SHORT).show()
//            true
//        }
//
//        instagram?.setOnPreferenceClickListener {
//
//            true
//        }
    }

}