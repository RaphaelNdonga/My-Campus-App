package com.example.android.mycampusapp.data.timetable.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.mycampusapp.data.SundayClass
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
class SundayDaoTest {
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
        val sundayClass = SundayClass(id = 1L, subject = "sunday subject",time = "10:00 am")
        database.timetableDao().insert(sundayClass)
        val sundayClass2 = database.timetableDao().getSundayClassById(1L)

        assertThat(sundayClass2.time,`is`("10:00 am"))
    }
    @Test
    fun update_delete(){
        var sundayClass = SundayClass(1L,"sunday subject","10:00 am")
        database.timetableDao().insert(sundayClass)

        val sundayClass2 = SundayClass(1L,"sunday subject2","10:00 am")
        database.timetableDao().update(sundayClass2)

        sundayClass = database.timetableDao().getSundayClassById(1L)

        assertThat(sundayClass.subject,`is`(sundayClass2.subject))

        database.timetableDao().delete(sundayClass)
        sundayClass = database.timetableDao().getSundayClassById(1L)

        assertThat(sundayClass,`is`(nullValue()))

    }
    @Test
    fun getsAll_DeletesAll(){
        val sundayClass = SundayClass(1L,"sunday_subject","10:00am")
        val sundayClass2 = SundayClass(2L,"sunday_subject2","11:00am")
        database.timetableDao().insert(sundayClass,sundayClass2)

        var sundayClasses = database.timetableDao().getAllSundayClasses()
        assertThat(sundayClasses[0].time,`is`("10:00am"))

        database.timetableDao().deleteAllSundayClasses()
        sundayClasses = database.timetableDao().getAllSundayClasses()

        assertThat(sundayClasses,`is`(emptyList()))
    }
    @Test
    fun getSingleLiveDataValues_getAllLiveDataValues(){
        val sundayClass = SundayClass(1L,"sunday_subject","10:00am")
        database.timetableDao().insert(sundayClass)

        val sundayClassLD = database.timetableDao().observeSundayClassById(1L)

        assertThat(sundayClassLD,`is`(Matchers.notNullValue()))

        val sundayClass2 = SundayClass(2L,"sunday_subject2","11:00am")
        database.timetableDao().insert(sundayClass2)

        val sundayClass2ListLD = database.timetableDao().observeAllSundayClasses()

        assertThat(sundayClass2ListLD, `is`(Matchers.notNullValue()))
    }
    @After
    fun closeDb(){
        database.close()
    }
}