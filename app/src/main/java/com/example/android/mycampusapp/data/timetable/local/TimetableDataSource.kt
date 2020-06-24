package com.example.android.mycampusapp.data.timetable.local

import androidx.lifecycle.LiveData
import com.example.android.mycampusapp.data.*

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

    suspend fun observeAllMondayClasses():LiveData<List<MondayClass>>
    suspend fun observeAllTuesdayClasses():LiveData<List<TuesdayClass>>
    suspend fun observeAllWednesdayClasses():LiveData<List<WednesdayClass>>
    suspend fun observeAllThursdayClasses():LiveData<List<ThursdayClass>>
    suspend fun observeAllFridayClasses():LiveData<List<FridayClass>>
    suspend fun observeAllSaturdayClasses():LiveData<List<SaturdayClass>>
    suspend fun observeAllSundayClasses():LiveData<List<SundayClass>>

    suspend fun deleteAllMondayClasses(mondayClass: MondayClass)
    suspend fun deleteAllTuesdayClasses(tuesdayClass: TuesdayClass)
    suspend fun deleteAllWednesdayClasses(wednesdayClass: WednesdayClass)
    suspend fun deleteAllThursdayClasses(thursdayClass: ThursdayClass)
    suspend fun deleteAllFridayClasses(fridayClass: FridayClass)
    suspend fun deleteAllSaturdayClasses(saturdayClass: SaturdayClass)
    suspend fun deleteAllSundayClasses(sundayClass: SundayClass)

    suspend fun getAllMondayClasses():List<MondayClass>
    suspend fun getAllTuesdayClasses():List<MondayClass>
    suspend fun getAllWednesdayClasses():List<MondayClass>
    suspend fun getAllThursdayClasses():List<MondayClass>
    suspend fun getAllFridayClasses():List<MondayClass>
    suspend fun getAllSaturdayClasses():List<MondayClass>
    suspend fun getAllSundayClasses():List<MondayClass>

}
