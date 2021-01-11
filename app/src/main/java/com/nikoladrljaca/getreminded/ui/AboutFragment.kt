package com.nikoladrljaca.getreminded.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.transition.Slide
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialElevationScale
import com.nikoladrljaca.getreminded.BuildConfig
import com.nikoladrljaca.getreminded.R
import com.nikoladrljaca.getreminded.databinding.FragmentAboutBinding
import com.nikoladrljaca.getreminded.utils.ANIM_DURATION

class AboutFragment : Fragment(R.layout.fragment_about) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab_create_new_reminder)
        val bottomAppBar = requireActivity().findViewById<BottomAppBar>(R.id.bottomAppBar)
        val binding = FragmentAboutBinding.bind(view)

        enterTransition = MaterialElevationScale(true).apply {
            duration = ANIM_DURATION
        }
        returnTransition = Slide().apply {
            addTarget(R.id.cv_about)
            duration = ANIM_DURATION
        }

        binding.apply {
            val versionName = BuildConfig.VERSION_NAME
            tvVersion.text = "Version $versionName"
        }

        fab.visibility = View.INVISIBLE
        bottomAppBar.visibility = View.INVISIBLE
    }
}