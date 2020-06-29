package com.example.android.mycampusapp.data.timetable.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.mycampusapp.data.WednesdayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDatabase
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class WednesdayDaoTest {
    private lateinit var database: TimetableDatabase
    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TimetableDatabase::class.java
        ).allowMainThreadQueries().build()
    }
    @Test
    fun insert_retrieveById(){
        val wednesdayClass = WednesdayClass(id = 1L, subject = "wednesday subject",time = "10:00 am")
        database.timetableDao().insert(wednesdayClass)
        val wednesdayClass2 = database.timetableDao().getWednesdayClassById(1L)

        assertThat(wednesdayClass2.time,`is`("10:00 am"))
    }
    @Test
    fun update_delete(){
        var wednesdayClass = WednesdayClass(1L,"wednesday subject","10:00 am")
        database.timetableDao().insert(wednesdayClass)

        val wednesdayClass2 = WednesdayClass(1L,"wednesday subject2","10:00 am")
        database.timetableDao().update(wednesdayClass2)

        wednesdayClass = database.timetableDao().getWednesdayClassById(1L)

        assertThat(wednesdayClass.subject,`is`(wednesdayClass2.subject))

        database.timetableDao().delete(wednesdayClass)
        wednesdayClass = database.timetableDao().getWednesdayClassById(1L)

        assertThat(wednesdayClass,`is`(nullValue()))

    }
    @Test
    fun getsAll_DeletesAll(){
        val wednesdayClass = WednesdayClass(1L,"wednesday_subject","10:00am")
        val wednesdayClass2 = WednesdayClass(2L,"wednesday_subject2","11:00am")
        database.timetableDao().insert(wednesdayClass,wednesdayClass2)

        var wednesdayClasses = database.timetableDao().getAllWednesdayClasses()
        assertThat(wednesdayClasses[0].time,`is`("10:00am"))

        database.timetableDao().deleteAllWednesdayClasses()
        wednesdayClasses = database.timetableDao().getAllWednesdayClasses()

        assertThat(wednesdayClasses,`is`(emptyList()))
    }
    @Test
    fun getSingleLiveDataValues_getAllLiveDataValues(){
        val wednesdayClass = WednesdayClass(1L,"wednesday_subject","10:00am")
        database.timetableDao().insert(wednesdayClass)

        val wednesdayClassLD = database.timetableDao().observeWednesdayClassById(1L)

        assertThat(wednesdayClassLD,`is`(Matchers.notNullValue()))

        val wednesdayClass2 = WednesdayClass(2L,"wednesday_subject2","11:00am")
        database.timetableDao().insert(wednesdayClass2)

        val wednesdayClass2ListLD = database.timetableDao().observeAllWednesdayClasses()

        assertThat(wednesdayClass2ListLD, `is`(Matchers.notNullValue()))
    }
    @After
    fun closeDb(){
        database.close()
    }
}