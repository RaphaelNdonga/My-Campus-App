package com.example.android.mycampusapp.data.timetable.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.mycampusapp.data.ThursdayClass
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
class ThursdayDaoTest {
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
        val thursdayClass = ThursdayClass(id = 1L, subject = "thursday subject",time = "10:00 am")
        database.timetableDao().insert(thursdayClass)
        val thursdayClass2 = database.timetableDao().getThursdayClassById(1L)

        assertThat(thursdayClass2.time,`is`("10:00 am"))
    }
    @Test
    fun update_delete(){
        var thursdayClass = ThursdayClass(1L,"thursday subject","10:00 am")
        database.timetableDao().insert(thursdayClass)

        val thursdayClass2 = ThursdayClass(1L,"thursday subject2","10:00 am")
        database.timetableDao().update(thursdayClass2)

        thursdayClass = database.timetableDao().getThursdayClassById(1L)

        assertThat(thursdayClass.subject,`is`(thursdayClass2.subject))

        database.timetableDao().delete(thursdayClass)
        thursdayClass = database.timetableDao().getThursdayClassById(1L)

        assertThat(thursdayClass,`is`(nullValue()))

    }
    @Test
    fun getsAll_DeletesAll(){
        val thursdayClass = ThursdayClass(1L,"thursday_subject","10:00am")
        val thursdayClass2 = ThursdayClass(2L,"thursday_subject2","11:00am")
        database.timetableDao().insert(thursdayClass,thursdayClass2)

        var thursdayClasses = database.timetableDao().getAllThursdayClasses()
        assertThat(thursdayClasses[0].time,`is`("10:00am"))

        database.timetableDao().deleteAllThursdayClasses()
        thursdayClasses = database.timetableDao().getAllThursdayClasses()

        assertThat(thursdayClasses,`is`(emptyList()))
    }
    @Test
    fun getSingleLiveDataValues_getAllLiveDataValues(){
        val thursdayClass = ThursdayClass(1L,"thursday_subject","10:00am")
        database.timetableDao().insert(thursdayClass)

        val thursdayClassLD = database.timetableDao().observeThursdayClassById(1L)

        assertThat(thursdayClassLD,`is`(Matchers.notNullValue()))

        val thursdayClass2 = ThursdayClass(2L,"thursday_subject2","11:00am")
        database.timetableDao().insert(thursdayClass2)

        val thursdayClass2ListLD = database.timetableDao().observeAllThursdayClasses()

        assertThat(thursdayClass2ListLD, `is`(Matchers.notNullValue()))
    }
    @After
    fun closeDb(){
        database.close()
    }
}