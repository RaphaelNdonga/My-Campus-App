package com.example.android.mycampusapp.timetable.data.timetable.local

import androidx.lifecycle.LiveData
import com.example.android.mycampusapp.timetable.data.*

interface TimetableDataSource {

    suspend fun addMondayClass(mondayClass: MondayClass)
    suspend fun addTuesdayClass(tuesdayClass: TuesdayClass)
    suspend fun addWednesdayClass(wednesdayClass: WednesdayClass)
    suspend fun addThursdayClass(thursdayClass: ThursdayClass)
    suspend fun addFridayClass(fridayClass: FridayClass)
    suspend fun addSaturdayClass(saturdayClass: SaturdayClass)
    suspend fun addSundayClass(sundayClass: SundayClass)

    suspend fun deleteMondayClass(mondayClass: MondayClass)
    suspend fun deleteTuesdayClass(tuesdayClass: TuesdayClass)
    suspend fun deleteWednesdayClass(wednesdayClass: WednesdayClass)
    suspend fun deleteThursdayClass(thursdayClass: ThursdayClass)
    suspend fun deleteFridayClass(fridayClass: FridayClass)
    suspend fun deleteSaturdayClass(saturdayClass: SaturdayClass)
    suspend fun deleteSundayClass(sundayClass: SundayClass)

    suspend fun updateMondayClass(mondayClass: MondayClass)
    suspend fun updateTuesdayClass(tuesdayClass: TuesdayClass)
    suspend fun updateWednesdayClass(wednesdayClass: WednesdayClass)
    suspend fun updateThursdayClass(thursdayClass: ThursdayClass)
    suspend fun updateFridayClass(fridayClass: FridayClass)
    suspend fun updateSaturdayClass(saturdayClass: SaturdayClass)
    suspend fun updateSundayClass(sundayClass: SundayClass)

    fun observeAllMondayClasses(): LiveData<List<MondayClass>>
    fun observeAllTuesdayClasses(): LiveData<List<TuesdayClass>>
    fun observeAllWednesdayClasses(): LiveData<List<WednesdayClass>>
    fun observeAllThursdayClasses(): LiveData<List<ThursdayClass>>
    fun observeAllFridayClasses(): LiveData<List<FridayClass>>
    fun observeAllSaturdayClasses(): LiveData<List<SaturdayClass>>
    fun observeAllSundayClasses(): LiveData<List<SundayClass>>

    suspend fun deleteAllMondayClasses()
    suspend fun deleteAllTuesdayClasses()
    suspend fun deleteAllWednesdayClasses()
    suspend fun deleteAllThursdayClasses()
    suspend fun deleteAllFridayClasses()
    suspend fun deleteAllSaturdayClasses()
    suspend fun deleteAllSundayClasses()

    suspend fun getAllMondayClasses(): List<MondayClass>?
    suspend fun getAllTuesdayClasses(): List<TuesdayClass>?
    suspend fun getAllWednesdayClasses(): List<WednesdayClass>?
    suspend fun getAllThursdayClasses(): List<ThursdayClass>?
    suspend fun getAllFridayClasses(): List<FridayClass>?
    suspend fun getAllSaturdayClasses(): List<SaturdayClass>?
    suspend fun getAllSundayClasses(): List<SundayClass>?

    suspend fun getMondayClassWithId(id:Long): MondayClass
    suspend fun getTuesdayClassWithId(id:Long): TuesdayClass
    suspend fun getWednesdayClassWithId(id:Long): WednesdayClass
    suspend fun getThursdayClassWithId(id:Long): ThursdayClass
    suspend fun getFridayClassWithId(id:Long): FridayClass
    suspend fun getSaturdayClassWithId(id:Long): SaturdayClass
    suspend fun getSundayClassWithId(id:Long): SundayClass
}
