package com.example.android.mycampusapp.data.timetable.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDatabase
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class MondayDaoTest {
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
        val mondayClass = MondayClass(id = 1L, subject = "monday subject",time = "10:00 am")
        database.timetableDao().insert(mondayClass)
        val mondayClass2 = database.timetableDao().getMondayClassById(1L)

        assertThat(mondayClass2.time,`is`("10:00 am"))
    }
    @Test
    fun update_delete(){
        var mondayClass = MondayClass(1L,"monday subject","10:00 am")
        database.timetableDao().insert(mondayClass)

        val mondayClass2 = MondayClass(1L,"monday subject2","10:00 am")
        database.timetableDao().update(mondayClass2)

        mondayClass = database.timetableDao().getMondayClassById(1L)

        assertThat(mondayClass.subject,`is`(mondayClass2.subject))

        database.timetableDao().delete(mondayClass)
        mondayClass = database.timetableDao().getMondayClassById(1L)

        assertThat(mondayClass,`is`(nullValue()))

    }
    @Test
    fun getsAll_DeletesAll(){
        val mondayClass = MondayClass(1L,"monday_subject","10:00am")
        val mondayClass2 = MondayClass(2L,"monday_subject2","11:00am")
        database.timetableDao().insert(mondayClass,mondayClass2)

        var mondayClasses = database.timetableDao().getAllMondayClasses()
        assertThat(mondayClasses[0].time,`is`("10:00am"))

        database.timetableDao().deleteAllMondayClasses()
        mondayClasses = database.timetableDao().getAllMondayClasses()

        assertThat(mondayClasses,`is`(emptyList()))
    }
    @Test
    fun getSingleLiveDataValues_getAllLiveDataValues(){
        val mondayClass = MondayClass(1L,"monday_subject","10:00am")
        database.timetableDao().insert(mondayClass)

        val mondayClassLD = database.timetableDao().observeMondayClassById(1L)

        assertThat(mondayClassLD,`is`(notNullValue()))

        val mondayClass2 = MondayClass(2L,"monday_subject2","11:00am")
        database.timetableDao().insert(mondayClass2)

        val mondayClass2ListLD = database.timetableDao().observeAllMondayClasses()

        assertThat(mondayClass2ListLD, `is`(notNullValue()))
    }
    @After
    fun closeDb(){
        database.close()
    }
}