package com.example.android.mycampusapp.data.timetable.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.mycampusapp.data.TuesdayClass
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
class TuesdayDaoTest {
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
        val tuesdayClass = TuesdayClass(id = 1L, subject = "tuesday subject",time = "10:00 am")
        database.timetableDao().insert(tuesdayClass)
        val tuesdayClass2 = database.timetableDao().getTuesdayClassById(1L)

        assertThat(tuesdayClass2.time,`is`("10:00 am"))
    }
    @Test
    fun update_delete(){
        var tuesdayClass = TuesdayClass(1L,"tuesday subject","10:00 am")
        database.timetableDao().insert(tuesdayClass)

        val tuesdayClass2 = TuesdayClass(1L,"tuesday subject2","10:00 am")
        database.timetableDao().update(tuesdayClass2)

        tuesdayClass = database.timetableDao().getTuesdayClassById(1L)

        assertThat(tuesdayClass.subject,`is`(tuesdayClass2.subject))

        database.timetableDao().delete(tuesdayClass)
        tuesdayClass = database.timetableDao().getTuesdayClassById(1L)

        assertThat(tuesdayClass,`is`(nullValue()))

    }
    @Test
    fun getsAll_DeletesAll(){
        val tuesdayClass = TuesdayClass(1L,"tuesday_subject","10:00am")
        val tuesdayClass2 = TuesdayClass(2L,"tuesday_subject2","11:00am")
        database.timetableDao().insert(tuesdayClass,tuesdayClass2)

        var tuesdayClasses = database.timetableDao().getAllTuesdayClasses()
        assertThat(tuesdayClasses[0].time,`is`("10:00am"))

        database.timetableDao().deleteAllTuesdayClasses()
        tuesdayClasses = database.timetableDao().getAllTuesdayClasses()

        assertThat(tuesdayClasses,`is`(emptyList()))
    }
    @Test
    fun getSingleLiveDataValues_getAllLiveDataValues(){
        val tuesdayClass = TuesdayClass(1L,"tuesday_subject","10:00am")
        database.timetableDao().insert(tuesdayClass)

        val tuesdayClassLD = database.timetableDao().observeTuesdayClassById(1L)

        assertThat(tuesdayClassLD,`is`(Matchers.notNullValue()))

        val tuesdayClass2 = TuesdayClass(2L,"tuesday_subject2","11:00am")
        database.timetableDao().insert(tuesdayClass2)

        val tuesdayClass2ListLD = database.timetableDao().observeAllTuesdayClasses()

        assertThat(tuesdayClass2ListLD, `is`(Matchers.notNullValue()))
    }
    @After
    fun closeDb(){
        database.close()
    }
}