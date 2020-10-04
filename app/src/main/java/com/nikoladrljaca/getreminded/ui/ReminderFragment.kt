package com.nikoladrljaca.getreminded.ui


import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nikoladrljaca.getreminded.R
import com.nikoladrljaca.getreminded.databinding.FragmentReminderBinding
import com.nikoladrljaca.getreminded.viewmodel.Reminder
import com.nikoladrljaca.getreminded.viewmodel.SharedViewModel
import java.text.SimpleDateFormat
import java.util.*

class ReminderFragment : Fragment(R.layout.fragment_reminder) {
    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private var reminderExists = false
    private var reminderId = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReminderBinding.bind(view)
        var date = Calendar.getInstance().timeInMillis
        val bottomAppBar = requireActivity().findViewById<BottomAppBar>(R.id.bottomAppBar)
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab_create_new_reminder)

        //check if text came from outside and process it
        if (requireArguments().getBoolean("sharedItemSent", false)) {
            bottomAppBar.visibility = View.INVISIBLE
            fab.visibility = View.INVISIBLE
            binding.etReminderNote.setText(
                requireArguments().getString("sharedString"),
                TextView.BufferType.EDITABLE
            )
        }

        sharedViewModel.displayReminder.observe(viewLifecycleOwner, {
            date = it.date
            binding.etReminderTitle.setText(it.title, TextView.BufferType.EDITABLE)
            binding.etReminderNote.setText(it.note, TextView.BufferType.EDITABLE)
            binding.tvReminderDate.text = getString(R.string.date_display, dateFromEpoch(date))
            reminderId = it.id!!
            reminderExists = true
        })

        sharedViewModel.clearReminder.observe(viewLifecycleOwner) {
            if (it) {
                binding.etReminderTitle.text.clear()
                binding.etReminderNote.text.clear()
                binding.tvReminderDate.text = ""
                reminderExists = false
                date = Calendar.getInstance().timeInMillis
            }
        }

        binding.btnSelectDate.setOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
            val picker = builder.build()
            picker.show(parentFragmentManager, picker.toString())

            picker.addOnPositiveButtonClickListener {
                date = it
                binding.tvReminderDate.text = getString(R.string.date_display, dateFromEpoch(date))
            }
        }

        binding.btnSave.setOnClickListener {
            if (TextUtils.isEmpty(binding.etReminderTitle.text)) {
                displayToastShort(getString(R.string.save_reminder_error))
            } else {
                //save to database and return to previous fragment
                val title = binding.etReminderTitle.text.toString()
                val note = binding.etReminderNote.text.toString()
                val reminder = Reminder(title, note, date)
                if (reminderExists) {
                    reminder.id = reminderId
                    sharedViewModel.updateEntry(reminder)
                } else sharedViewModel.insert(reminder)

                //return to mainFragment
                //close keyboard
                binding.etReminderTitle.clearFocus()
                val input = requireActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                input.hideSoftInputFromWindow(binding.etReminderTitle.windowToken, 0)

                findNavController().navigate(R.id.action_reminderFragment_to_mainFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dateFromEpoch(epoch: Long): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.US)
        val date = Date(epoch)
        return sdf.format(date)
    }

    private fun displayToastShort(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}