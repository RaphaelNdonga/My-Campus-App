package com.example.android.mycampusapp.data.timetable.local.timetableLocalDataSource

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDatabase
import com.example.android.mycampusapp.data.timetable.local.TimetableLocalDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.After
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
@MediumTest
class MondayLocalDataSourceTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var timetableLocalDataSource: TimetableLocalDataSource
    private lateinit var database: TimetableDatabase

    @Before
    fun initDataSource() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TimetableDatabase::class.java
        ).allowMainThreadQueries().build()
        timetableLocalDataSource =
            TimetableLocalDataSource(
                database.timetableDao()
            )
    }

    @Test
    fun addMondayClass() = runBlocking {
        val mondayClass = MondayClass(subject = "monday_subject", time = "monday_time")

        timetableLocalDataSource.addMondayClass(mondayClass)

        val mondayClasses = timetableLocalDataSource.getAllMondayClasses()

        assertThat(mondayClasses?.get(0)?.subject, `is`(mondayClass.subject))
    }

    @Test
    fun deleteAllMondayClasses() = runBlocking {
        val mondayClass = MondayClass(subject = "monday_subject", time = "monday_time")
        val mondayClass2 = MondayClass(subject = "monday_subject2", time = "monday_time2")

        timetableLocalDataSource.addMondayClass(mondayClass)
        timetableLocalDataSource.addMondayClass(mondayClass2)

        timetableLocalDataSource.deleteAllMondayClasses()
        val mondayClasses = timetableLocalDataSource.getAllMondayClasses()

        assertThat(mondayClasses, `is`(emptyList()))
    }

    @Test
    fun deleteMondayClass() = runBlocking {
        val mondayClass = MondayClass(id = 1, subject = "monday_subject", time = "monday_time")

        timetableLocalDataSource.addMondayClass(mondayClass)
        timetableLocalDataSource.deleteMondayClass(mondayClass)
        val mondayClasses = timetableLocalDataSource.getAllMondayClasses()

        assertThat(mondayClasses, `is`(emptyList()))
    }

    @Test
    fun updateMondayClass() = runBlocking {
        val mondayClass = MondayClass(1, "monday_subject", "monday_time")
        val mondayClass2 = MondayClass(1, "monday_subject2", "monday_time2")

        timetableLocalDataSource.addMondayClass(mondayClass)
        timetableLocalDataSource.updateMondayClass(mondayClass2)
        val mondayClasses = timetableLocalDataSource.getAllMondayClasses()

        assertThat(mondayClasses?.get(0)?.subject, `is`(mondayClass2.subject))
    }

    @After
    fun closeDb() {
        database.close()
    }
}