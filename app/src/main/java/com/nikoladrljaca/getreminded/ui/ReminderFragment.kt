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
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
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

    private val args: ReminderFragmentArgs by navArgs()
    private var reminderExists = false
    private var reminderId = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReminderBinding.bind(view)
        var date = Calendar.getInstance().timeInMillis

        if (args.reminderId < 0) {
            //display empty screen
        }
        //an argument was passed
        if (args.reminderId > 0) {
            sharedViewModel.setDisplayReminder(args.reminderId)
            sharedViewModel.displayReminder.observe(viewLifecycleOwner){
                date = it.date
                binding.apply {
                    etReminderTitle.setText(it.title, TextView.BufferType.EDITABLE)
                    etReminderNote.setText(it.note, TextView.BufferType.EDITABLE)
                    tvReminderDate.text = dateFromEpoch(date)
                    reminderId = it.id!!
                    reminderExists = true
                }
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

                findNavController().navigateUp()
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