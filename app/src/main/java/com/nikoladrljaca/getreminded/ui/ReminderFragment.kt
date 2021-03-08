package com.nikoladrljaca.getreminded.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import com.google.android.material.color.MaterialColors
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.transition.MaterialContainerTransform
import com.nikoladrljaca.getreminded.R
import com.nikoladrljaca.getreminded.databinding.FragmentReminderBinding
import com.nikoladrljaca.getreminded.utils.*
import com.nikoladrljaca.getreminded.viewmodel.SharedViewModel
import dev.sasikanth.colorsheet.ColorSheet
import java.util.*

class ReminderFragment : Fragment(R.layout.fragment_reminder) {
    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val args: ReminderFragmentArgs by navArgs()
    private var reminderExists = false
    private var reminderId = 0
    private var date = Calendar.getInstance().timeInMillis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //piece of code written inside addCallback gets executed when the back button is pressed
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            sharedViewModel.insert(
                title = binding.etReminderTitle.text.toString(),
                note = binding.etReminderNote.text.toString(),
                exists = args.reminderId > 0,
                id = reminderId,
                date = date,

            )
            findNavController().navigateUp()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReminderBinding.bind(view)

        if (args.reminderId < 0) {
            //display empty screen
            //transitions
            enterTransition = MaterialContainerTransform().apply {
                startView = requireActivity().findViewById(R.id.fab_create_new_reminder)
                endView = binding.reminderDetailCard
                setAllContainerColors(MaterialColors.getColor(binding.root, R.attr.colorSurface))
                duration = ANIM_DURATION
                interpolator = FastOutSlowInInterpolator()
            }

            returnTransition = Slide().apply {
                addTarget(R.id.reminder_detail_card)
                duration = ANIM_DURATION
            }
        }
        //an argument was passed
        if (args.reminderId > 0) {
            sharedElementEnterTransition = MaterialContainerTransform().apply {
                drawingViewId = R.id.nav_host_frag
                duration = ANIM_DURATION
                interpolator = FastOutSlowInInterpolator()
                setAllContainerColors(MaterialColors.getColor(binding.root, R.attr.colorSurface))
            }

            sharedViewModel.setDisplayReminder(args.reminderId)
            sharedViewModel.displayReminder.observe(viewLifecycleOwner) {
                date = it.date
                binding.apply {
                    etReminderTitle.setText(it.title, TextView.BufferType.EDITABLE)
                    etReminderNote.setText(it.note, TextView.BufferType.EDITABLE)
                    tvReminderDate.text = dateFromEpoch(date)
                    constraintLayout.setBackgroundColor(it.color)
                    reminderId = it.id!!
                    reminderExists = true
                }
            }
        }

        binding.apply {
            btnSelectDate.setOnClickListener {
                val builder = MaterialDatePicker.Builder.datePicker()
                val picker = builder.build()
                picker.show(parentFragmentManager, picker.toString())

                picker.addOnPositiveButtonClickListener {
                    date = it
                    binding.tvReminderDate.text = getString(R.string.date_display, dateFromEpoch(date))
                }
            }

            btnSelectColor.setOnClickListener {
                ColorSheet().colorPicker(
                    colors = colorListInInts,
                    noColorOption = true,
                    listener = { color ->
                        sharedViewModel.setCardColor(color)
                        constraintLayout.setBackgroundColor(color)
                    }
                ).show(requireActivity().supportFragmentManager)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}