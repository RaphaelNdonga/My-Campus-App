package com.example.android.mycampusapp.data.timetable.local

import androidx.lifecycle.LiveData
import com.example.android.mycampusapp.data.*

interface TimetableDataSource {

    fun addMondayClass(mondayClass: MondayClass)
    fun addTuesdayClass(tuesdayClass: TuesdayClass)
    fun addWednesdayClass(wednesdayClass: WednesdayClass)
    fun addThursdayClass(thursdayClass: ThursdayClass)
    fun addFridayClass(fridayClass: FridayClass)
    fun addSaturdayClass(saturdayClass: SaturdayClass)
    fun addSundayClass(sundayClass: SundayClass)

    fun deleteMondayClass(mondayClass: MondayClass)
    fun deleteTuesdayClass(tuesdayClass: TuesdayClass)
    fun deleteWednesdayClass(wednesdayClass: WednesdayClass)
    fun deleteThursdayClass(thursdayClass: ThursdayClass)
    fun deleteFridayClass(fridayClass: FridayClass)
    fun deleteSaturdayClass(saturdayClass: SaturdayClass)
    fun deleteSundayClass(sundayClass: SundayClass)

    fun updateMondayClass(mondayClass: MondayClass)
    fun updateTuesdayClass(tuesdayClass: TuesdayClass)
    fun updateWednesdayClass(wednesdayClass: WednesdayClass)
    fun updateThursdayClass(thursdayClass: ThursdayClass)
    fun updateFridayClass(fridayClass: FridayClass)
    fun updateSaturdayClass(saturdayClass: SaturdayClass)
    fun updateSundayClass(sundayClass: SundayClass)

    fun observeAllMondayClasses():LiveData<List<MondayClass>>
    fun observeAllTuesdayClasses():LiveData<List<TuesdayClass>>
    fun observeAllWednesdayClasses():LiveData<List<WednesdayClass>>
    fun observeAllThursdayClasses():LiveData<List<ThursdayClass>>
    fun observeAllFridayClasses():LiveData<List<FridayClass>>
    fun observeAllSaturdayClasses():LiveData<List<SaturdayClass>>
    fun observeAllSundayClasses():LiveData<List<SundayClass>>

    fun deleteAllMondayClasses(mondayClass: MondayClass)
    fun deleteAllTuesdayClasses(tuesdayClass: TuesdayClass)
    fun deleteAllWednesdayClasses(wednesdayClass: WednesdayClass)
    fun deleteAllThursdayClasses(thursdayClass: ThursdayClass)
    fun deleteAllFridayClasses(fridayClass: FridayClass)
    fun deleteAllSaturdayClasses(saturdayClass: SaturdayClass)
    fun deleteAllSundayClasses(sundayClass: SundayClass)

    fun getMondayClassWithId(id:Long):MondayClass?


}
