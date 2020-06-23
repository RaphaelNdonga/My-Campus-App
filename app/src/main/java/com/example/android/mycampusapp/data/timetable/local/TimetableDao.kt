package com.example.android.mycampusapp.data.timetable.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.mycampusapp.data.*

@Dao
interface TimetableDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg obj: MondayClass)

    @Update
    fun update(obj: MondayClass)

    @Delete
    fun delete(obj: MondayClass)

    @Query("DELETE FROM monday_table")
    fun deleteAllMondayClasses()

    @Query("SELECT * FROM monday_table WHERE id = :mondayId")
    fun getMondayClassById(mondayId: Long): MondayClass

    @Query("SELECT * FROM monday_table WHERE id = :mondayId")
    fun observeMondayClassById(mondayId: Long):LiveData<MondayClass>

    @Query("SELECT * FROM monday_table")
    fun getAllMondayClasses():List<MondayClass>

    @Query("SELECT * FROM monday_table")
    fun observeAllMondayClasses():LiveData<List<MondayClass>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg obj: TuesdayClass)

    @Update
    fun update(obj: TuesdayClass)

    @Delete
    fun delete(obj: TuesdayClass)

    @Query("DELETE FROM tuesday_table")
    fun deleteAllTuesdayClasses()

    @Query("SELECT * FROM tuesday_table WHERE id = :tuesdayId")
    fun getTuesdayClassById(tuesdayId: Long): TuesdayClass

    @Query("SELECT * FROM tuesday_table WHERE id = :tuesdayId")
    fun observeTuesdayClassById(tuesdayId: Long): LiveData<TuesdayClass>

    @Query("SELECT * FROM tuesday_table")
    fun getAllTuesdayClasses(): List<TuesdayClass>

    @Query("SELECT * FROM tuesday_table")
    fun observeAllTuesdayClasses(): LiveData<List<TuesdayClass>>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg obj: WednesdayClass)

    @Update
    fun update(obj: WednesdayClass)

    @Delete
    fun delete(obj: WednesdayClass)

    @Query("DELETE FROM wednesday_table")
    fun deleteAllWednesdayClasses()

    @Query("SELECT * FROM wednesday_table WHERE id = :wednesdayId")
    fun getWednesdayClassById(wednesdayId: Long): WednesdayClass

    @Query("SELECT * FROM wednesday_table WHERE id = :wednesdayId")
    fun observeWednesdayClassById(wednesdayId: Long): LiveData<WednesdayClass>

    @Query("SELECT * FROM wednesday_table")
    fun getAllWednesdayClasses(): List<WednesdayClass>

    @Query("SELECT * FROM wednesday_table")
    fun observeAllWednesdayClasses(): LiveData<List<WednesdayClass>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg obj: ThursdayClass)

    @Update
    fun update(obj: ThursdayClass)

    @Delete
    fun delete(obj: ThursdayClass)

    @Query("DELETE FROM thursday_table")
    fun deleteAllThursdayClasses()

    @Query("SELECT * FROM thursday_table WHERE id = :thursdayId")
    fun getThursdayClassById(thursdayId: Long): ThursdayClass

    @Query("SELECT * FROM thursday_table WHERE id = :thursdayId")
    fun observeThursdayClassById(thursdayId: Long): LiveData<ThursdayClass>

    @Query("SELECT * FROM thursday_table")
    fun getAllThursdayClasses(): List<ThursdayClass>

    @Query("SELECT * FROM thursday_table")
    fun observeAllThursdayClasses(): LiveData<List<ThursdayClass>>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg obj: FridayClass)

    @Update
    fun update(obj: FridayClass)

    @Delete
    fun delete(obj: FridayClass)

    @Query("DELETE FROM friday_table")
    fun deleteAllFridayClasses()

    @Query("SELECT * FROM friday_table WHERE id = :fridayId")
    fun getFridayClassById(fridayId: Long): FridayClass

    @Query("SELECT * FROM friday_table WHERE id = :fridayId")
    fun observeFridayClassById(fridayId: Long): LiveData<FridayClass>

    @Query("SELECT * FROM friday_table")
    fun getAllFridayClasses(): List<FridayClass>

    @Query("SELECT * FROM friday_table")
    fun observeAllFridayClasses(): LiveData<List<FridayClass>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg obj: SaturdayClass)

    @Update
    fun update(obj: SaturdayClass)

    @Delete
    fun delete(obj: SaturdayClass)

    @Query("DELETE FROM saturday_table")
    fun deleteAllSaturdayClasses()

    @Query("SELECT * FROM saturday_table WHERE id = :saturdayId")
    fun getSaturdayClassById(saturdayId: Long): SaturdayClass

    @Query("SELECT * FROM saturday_table WHERE id = :saturdayId")
    fun observeSaturdayClassById(saturdayId: Long): LiveData<SaturdayClass>

    @Query("SELECT * FROM saturday_table")
    fun getAllSaturdayClasses(): List<SaturdayClass>

    @Query("SELECT * FROM saturday_table")
    fun observeAllSaturdayClasses(): LiveData<List<SaturdayClass>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg obj: SundayClass)

    @Update
    fun update(obj: SundayClass)

    @Delete
    fun delete(obj: SundayClass)

    @Query("DELETE FROM sunday_table")
    fun deleteAllSundayClasses()

    @Query("SELECT * FROM sunday_table WHERE id = :sundayId")
    fun getSundayClassById(sundayId: Long): SundayClass

    @Query("SELECT * FROM sunday_table WHERE id = :sundayId")
    fun observeSundayClassById(sundayId: Long): LiveData<SundayClass>

    @Query("SELECT * FROM sunday_table")
    fun getAllSundayClasses(): List<SundayClass>

    @Query("SELECT * FROM sunday_table")
    fun observeAllSundayClasses(): LiveData<List<SundayClass>>
}


