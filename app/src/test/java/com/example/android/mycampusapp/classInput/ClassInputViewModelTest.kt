package com.example.android.mycampusapp.classInput

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.mycampusapp.MainCoroutineRule
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.data.timetable.local.FakeTimetableRepository
import com.example.android.mycampusapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ClassInputViewModelTest{
    private lateinit var viewModel:ClassInputViewModel
    private lateinit var repository: FakeTimetableRepository

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() = runBlocking{
        repository  = FakeTimetableRepository()
        val mondayClass1 = MondayClass(1,"monday_subject1","monday_time1")
        val mondayClass2 = MondayClass(2,"monday_subject2","monday_time2")
        val mondayClass3 = MondayClass(3,"monday_subject3","monday_time3")

        repository.addMondayClass(mondayClass1)
        repository.addMondayClass(mondayClass2)
        repository.addMondayClass(mondayClass3)

        viewModel = ClassInputViewModel(repository)
    }
    @Test
    fun navigateToTimetable_setsNewNavigatorEvent(){
        viewModel.navigateToTimetable()
        val value = viewModel.navigator.getOrAwaitValue()

        assertThat(value.getContentIfNotHandled(),`is`(notNullValue()))
    }

    @Test
    fun addMondayClass_toDb_showsSnackbarMessage() = runBlocking{
        val subject = "monday_subject"
        val time = "monday_time"
        viewModel.addMondayClass(subject,time)

        val mondayClasses = repository.getAllMondayClasses()
        assertThat(mondayClasses?.get(3)?.subject ,`is`(subject))

        val value = viewModel.snackbarText.getOrAwaitValue()
        assertThat(value.getContentIfNotHandled(), `is`(R.string.monday_saved))
    }
}