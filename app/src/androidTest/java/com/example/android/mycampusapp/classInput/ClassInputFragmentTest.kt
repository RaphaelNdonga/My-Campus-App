package com.example.android.mycampusapp.classInput

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.mycampusapp.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class ClassInputFragmentTest{
    @Test
    fun classInputDetails_displayedInUI(){
        launchFragmentInContainer<ClassInputFragment>(Bundle(),
            R.style.Theme_MyCampusApp
        )
        onView(withId(R.id.class_subject_input)).check(matches(isDisplayed()))
        onView(withId(R.id.class_time_input)).check(matches(isDisplayed()))
        onView(withId(R.id.open_book_image)).check(matches(isDisplayed()))
        onView(withId(R.id.save_button)).check(matches(isDisplayed()))
    }
}