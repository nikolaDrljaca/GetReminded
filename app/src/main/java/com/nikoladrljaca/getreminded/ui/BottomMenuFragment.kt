package com.nikoladrljaca.getreminded.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.navigation.fragment.findNavController

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.nikoladrljaca.getreminded.R
import com.nikoladrljaca.getreminded.databinding.FragmentBottomMenuBinding

class BottomMenuFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentBottomMenuBinding.bind(view)

        binding.apply {
            tvAbout.setOnClickListener {
                val action = BottomMenuFragmentDirections.actionBottomMenuFragmentToAboutFragment()
                findNavController().navigate(action)
            }

            tvDeletedReminders.setOnClickListener {
                val action = BottomMenuFragmentDirections.actionBottomMenuFragmentToDeletedRemindersFragment()
                findNavController().navigate(action)
            }

            tvSettings.setOnClickListener {
                val action = BottomMenuFragmentDirections.actionBottomMenuFragmentToSettingsFragment()
                findNavController().navigate(action)
            }
        }
    }

}