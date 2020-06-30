package com.example.android.mycampusapp.timetable.days.monday

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.mycampusapp.MainCoroutineRule
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.data.timetable.local.FakeTimetableRepository
import com.example.android.mycampusapp.data.timetable.local.TimetableDatabase
import com.example.android.mycampusapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MondayViewModelTest{
    private lateinit var viewModel:MondayViewModel
    private lateinit var repository:FakeTimetableRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() = runBlocking{
        repository = FakeTimetableRepository()
        val mondayClass1 = MondayClass(subject = "monday_subject1",time = "monday_time1")
        val mondayClass2 = MondayClass(subject = "monday_subject2",time = "monday_time2")
        val mondayClass3 = MondayClass(subject = "monday_subject3",time = "monday_time3")

        repository.addMondayClass(mondayClass1)
        repository.addMondayClass(mondayClass2)
        repository.addMondayClass(mondayClass3)

        viewModel = MondayViewModel(repository)
    }
    @Test
    fun mondayClasses_displayed(){
        val mondayClasses = viewModel.mondayClasses.getOrAwaitValue()
        assertThat(mondayClasses,`is`(notNullValue()))
    }
    @Test
    fun navigateToSelectedClass(){
        val mondayClass = MondayClass(subject = "monday_subject",time = "monday_time")
        viewModel.displayMondayClassDetails(mondayClass)

        assertThat(viewModel.navigateToSelectedClass, `is`(notNullValue()))
    }

}