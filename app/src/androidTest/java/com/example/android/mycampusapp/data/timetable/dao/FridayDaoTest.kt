package com.example.android.mycampusapp.data.timetable.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.mycampusapp.data.FridayClass
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
class FridayDaoTest {
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
        val fridayClass = FridayClass(subject = "friday subject",time = "10:00 am")
        database.timetableDao().insert(fridayClass)
        val fridayClass2 = database.timetableDao().getFridayClassById(fridayClass.id)

        assertThat(fridayClass2.time,`is`("10:00 am"))
    }
    @Test
    fun update_delete(){
        var fridayClass = FridayClass(1L,"friday subject","10:00 am")
        database.timetableDao().insert(fridayClass)

        val fridayClass2 = FridayClass(1L,"friday subject2","10:00 am")
        database.timetableDao().update(fridayClass2)

        fridayClass = database.timetableDao().getFridayClassById(1L)

        assertThat(fridayClass.subject,`is`(fridayClass2.subject))

        database.timetableDao().delete(fridayClass)
        fridayClass = database.timetableDao().getFridayClassById(1L)

        assertThat(fridayClass,`is`(nullValue()))

    }
    @Test
    fun getsAll_DeletesAll(){
        val fridayClass = FridayClass(1L,"friday_subject","10:00am")
        val fridayClass2 = FridayClass(2L,"friday_subject2","11:00am")
        database.timetableDao().insert(fridayClass,fridayClass2)

        var fridayClasses = database.timetableDao().getAllFridayClasses()
        assertThat(fridayClasses[0].time,`is`("10:00am"))

        database.timetableDao().deleteAllFridayClasses()
        fridayClasses = database.timetableDao().getAllFridayClasses()

        assertThat(fridayClasses,`is`(emptyList()))
    }
    @Test
    fun getSingleLiveDataValues_getAllLiveDataValues(){
        val fridayClass = FridayClass(1L,"friday_subject","10:00am")
        database.timetableDao().insert(fridayClass)

        val fridayClassLD = database.timetableDao().observeFridayClassById(1L)

        assertThat(fridayClassLD,`is`(Matchers.notNullValue()))

        val fridayClass2 = FridayClass(2L,"friday_subject2","11:00am")
        database.timetableDao().insert(fridayClass2)

        val fridayClass2ListLD = database.timetableDao().observeAllFridayClasses()

        assertThat(fridayClass2ListLD, `is`(Matchers.notNullValue()))
    }
    @After
    fun closeDb(){
        database.close()
    }
}