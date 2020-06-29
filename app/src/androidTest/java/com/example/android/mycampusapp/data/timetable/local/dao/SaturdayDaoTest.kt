package com.example.android.mycampusapp.data.timetable.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.mycampusapp.data.SaturdayClass
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
class SaturdayDaoTest {
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
        val saturdayClass = SaturdayClass(id = 1L, subject = "saturday subject",time = "10:00 am")
        database.timetableDao().insert(saturdayClass)
        val saturdayClass2 = database.timetableDao().getSaturdayClassById(1L)

        assertThat(saturdayClass2.time,`is`("10:00 am"))
    }
    @Test
    fun update_delete(){
        var saturdayClass = SaturdayClass(1L,"saturday subject","10:00 am")
        database.timetableDao().insert(saturdayClass)

        val saturdayClass2 = SaturdayClass(1L,"saturday subject2","10:00 am")
        database.timetableDao().update(saturdayClass2)

        saturdayClass = database.timetableDao().getSaturdayClassById(1L)

        assertThat(saturdayClass.subject,`is`(saturdayClass2.subject))

        database.timetableDao().delete(saturdayClass)
        saturdayClass = database.timetableDao().getSaturdayClassById(1L)

        assertThat(saturdayClass,`is`(nullValue()))

    }
    @Test
    fun getsAll_DeletesAll(){
        val saturdayClass = SaturdayClass(1L,"saturday_subject","10:00am")
        val saturdayClass2 = SaturdayClass(2L,"saturday_subject2","11:00am")
        database.timetableDao().insert(saturdayClass,saturdayClass2)

        var saturdayClasses = database.timetableDao().getAllSaturdayClasses()
        assertThat(saturdayClasses[0].time,`is`("10:00am"))

        database.timetableDao().deleteAllSaturdayClasses()
        saturdayClasses = database.timetableDao().getAllSaturdayClasses()

        assertThat(saturdayClasses,`is`(emptyList()))
    }
    @Test
    fun getSingleLiveDataValues_getAllLiveDataValues(){
        val saturdayClass = SaturdayClass(1L,"saturday_subject","10:00am")
        database.timetableDao().insert(saturdayClass)

        val saturdayClassLD = database.timetableDao().observeSaturdayClassById(1L)

        assertThat(saturdayClassLD,`is`(Matchers.notNullValue()))

        val saturdayClass2 = SaturdayClass(2L,"saturday_subject2","11:00am")
        database.timetableDao().insert(saturdayClass2)

        val saturdayClass2ListLD = database.timetableDao().observeAllSaturdayClasses()

        assertThat(saturdayClass2ListLD, `is`(Matchers.notNullValue()))
    }
    @After
    fun closeDb(){
        database.close()
    }
}