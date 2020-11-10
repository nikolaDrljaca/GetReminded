package com.nikoladrljaca.getreminded.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import com.nikoladrljaca.getreminded.R

class AppIntroActivity: AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //1
        addSlide(AppIntroFragment.newInstance(
            title = "Welcome!",
            description = "GetReminded helps you keep notes and get notified of them at your desired time.",
            titleTypefaceFontRes = R.font.montserrat_bold,
            descriptionTypefaceFontRes = R.font.montserrat_regular,
            imageDrawable = R.drawable.iconfinder_bell,
            backgroundColor = resources.getColor(R.color.colorAccent)
        ))
        //2
        addSlide(AppIntroFragment.newInstance(
            title = "Pick your time! Any Time!",
            description = "Ability to always change the reminder time.",
            titleTypefaceFontRes = R.font.montserrat_bold,
            descriptionTypefaceFontRes = R.font.montserrat_regular,
            imageDrawable = R.drawable.timepicker,
            backgroundColor = resources.getColor(R.color.colorAccent)
        ))
        //3
        addSlide(AppIntroFragment.newInstance(
            title = "Red? Green? Blue?",
            description = "We all need that RGB. And some other colors.",
            titleTypefaceFontRes = R.font.montserrat_bold,
            descriptionTypefaceFontRes = R.font.montserrat_regular,
            imageDrawable = R.drawable.choose_color,
            backgroundColor = resources.getColor(R.color.colorAccent)
        ))
        //4
        addSlide(AppIntroFragment.newInstance(
            title = "Finish!",
            description = "Let's get going!",
            titleTypefaceFontRes = R.font.montserrat_bold,
            descriptionTypefaceFontRes = R.font.montserrat_regular,
            //imageDrawable = R.drawable.soup,
            backgroundColor = resources.getColor(R.color.colorAccent)
        ))
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        finish()
    }
}