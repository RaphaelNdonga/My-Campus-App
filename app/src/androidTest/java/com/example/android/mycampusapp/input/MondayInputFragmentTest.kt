package com.example.android.mycampusapp.input

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.input.monday.MondayInputFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MondayInputFragmentTest{

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun classInputDetails_displayedInUI(){
        launchFragmentInContainer<MondayInputFragment>(Bundle(),
            R.style.Theme_MyCampusApp
        )
        onView(withId(R.id.class_subject_input)).check(matches(isDisplayed()))
        onView(withId(R.id.class_time_input)).check(matches(isDisplayed()))
        onView(withId(R.id.open_book_image)).check(matches(isDisplayed()))
        onView(withId(R.id.save_button)).check(matches(isDisplayed()))
    }
}