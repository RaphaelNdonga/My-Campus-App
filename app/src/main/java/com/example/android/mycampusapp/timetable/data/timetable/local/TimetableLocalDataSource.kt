package com.example.android.mycampusapp.timetable.data.timetable.local

import androidx.lifecycle.LiveData
import com.example.android.mycampusapp.timetable.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TimetableLocalDataSource @Inject constructor(private val timetableDao: TimetableDao) :
    TimetableDataSource {

    override suspend fun addMondayClass(mondayClass: MondayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.insert(mondayClass)
        }
    }


    override suspend fun addTuesdayClass(tuesdayClass: TuesdayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.insert(tuesdayClass)
        }
    }


    override suspend fun addWednesdayClass(wednesdayClass: WednesdayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.insert(wednesdayClass)
        }
    }

    override suspend fun addThursdayClass(thursdayClass: ThursdayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.insert(thursdayClass)
        }
    }

    override suspend fun addFridayClass(fridayClass: FridayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.insert(fridayClass)
        }
    }

    override suspend fun addSaturdayClass(saturdayClass: SaturdayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.insert(saturdayClass)
        }
    }

    override suspend fun addSundayClass(sundayClass: SundayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.insert(sundayClass)
        }
    }

    override suspend fun deleteMondayClass(mondayClass: MondayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.delete(mondayClass)
        }
    }

    override suspend fun deleteTuesdayClass(tuesdayClass: TuesdayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.delete(tuesdayClass)
        }
    }

    override suspend fun deleteWednesdayClass(wednesdayClass: WednesdayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.delete(wednesdayClass)
        }
    }

    override suspend fun deleteThursdayClass(thursdayClass: ThursdayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.delete(thursdayClass)
        }
    }

    override suspend fun deleteFridayClass(fridayClass: FridayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.delete(fridayClass)
        }
    }

    override suspend fun deleteSaturdayClass(saturdayClass: SaturdayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.delete(saturdayClass)
        }
    }

    override suspend fun deleteSundayClass(sundayClass: SundayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.delete(sundayClass)
        }
    }

    override suspend fun updateMondayClass(mondayClass: MondayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.update(mondayClass)
        }
    }

    override suspend fun updateTuesdayClass(tuesdayClass: TuesdayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.update(tuesdayClass)
        }
    }

    override suspend fun updateWednesdayClass(wednesdayClass: WednesdayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.update(wednesdayClass)
        }
    }

    override suspend fun updateThursdayClass(thursdayClass: ThursdayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.update(thursdayClass)
        }
    }

    override suspend fun updateFridayClass(fridayClass: FridayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.update(fridayClass)
        }
    }

    override suspend fun updateSaturdayClass(saturdayClass: SaturdayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.update(saturdayClass)
        }
    }

    override suspend fun updateSundayClass(sundayClass: SundayClass) {

        withContext(Dispatchers.IO) {
            timetableDao.update(sundayClass)
        }
    }

    override fun observeAllMondayClasses(): LiveData<List<MondayClass>> {
        return timetableDao.observeAllMondayClasses()
    }

    override fun observeAllTuesdayClasses(): LiveData<List<TuesdayClass>> {
        return timetableDao.observeAllTuesdayClasses()
    }

    override fun observeAllWednesdayClasses(): LiveData<List<WednesdayClass>> {
        return timetableDao.observeAllWednesdayClasses()
    }

    override fun observeAllThursdayClasses(): LiveData<List<ThursdayClass>> {
        return timetableDao.observeAllThursdayClasses()
    }

    override fun observeAllFridayClasses(): LiveData<List<FridayClass>> {
        return timetableDao.observeAllFridayClasses()
    }

    override fun observeAllSaturdayClasses(): LiveData<List<SaturdayClass>> {
        return timetableDao.observeAllSaturdayClasses()
    }

    override fun observeAllSundayClasses(): LiveData<List<SundayClass>> {
        return timetableDao.observeAllSundayClasses()
    }


    override suspend fun deleteAllMondayClasses() {

        withContext(Dispatchers.IO) {
            timetableDao.deleteAllMondayClasses()
        }
    }

    override suspend fun deleteAllTuesdayClasses() {

        withContext(Dispatchers.IO) {
            timetableDao.deleteAllTuesdayClasses()
        }
    }

    override suspend fun deleteAllWednesdayClasses() {

        withContext(Dispatchers.IO) {
            timetableDao.deleteAllWednesdayClasses()
        }
    }

    override suspend fun deleteAllThursdayClasses() {

        withContext(Dispatchers.IO) {
            timetableDao.deleteAllThursdayClasses()
        }
    }


    override suspend fun deleteAllFridayClasses() {

        withContext(Dispatchers.IO) {
            timetableDao.deleteAllFridayClasses()
        }
    }

    override suspend fun deleteAllSaturdayClasses() {

        withContext(Dispatchers.IO) {
            timetableDao.deleteAllSaturdayClasses()
        }
    }

    override suspend fun deleteAllSundayClasses() {

        withContext(Dispatchers.IO) {
            timetableDao.deleteAllSundayClasses()
        }
    }

    override suspend fun getAllMondayClasses(): List<MondayClass>? {
        var mondayClasses: List<MondayClass>
        withContext(Dispatchers.IO) {
            mondayClasses = timetableDao.getAllMondayClasses()
        }
        return mondayClasses
    }

    override suspend fun getAllTuesdayClasses(): List<TuesdayClass>? {
        var tuesdayClasses: List<TuesdayClass>
        withContext(Dispatchers.IO) {
            tuesdayClasses = timetableDao.getAllTuesdayClasses()
        }
        return tuesdayClasses
    }

    override suspend fun getAllWednesdayClasses(): List<WednesdayClass>? {
        var wednesdayClasses: List<WednesdayClass>
        withContext(Dispatchers.IO) {
            wednesdayClasses = timetableDao.getAllWednesdayClasses()
        }
        return wednesdayClasses
    }

    override suspend fun getAllThursdayClasses(): List<ThursdayClass>? {
        var thursdayClasses: List<ThursdayClass>
        withContext(Dispatchers.IO) {
            thursdayClasses = timetableDao.getAllThursdayClasses()
        }
        return thursdayClasses
    }

    override suspend fun getAllFridayClasses(): List<FridayClass>? {
        var fridayClasses: List<FridayClass>
        withContext(Dispatchers.IO) {
            fridayClasses = timetableDao.getAllFridayClasses()
        }
        return fridayClasses
    }

    override suspend fun getAllSaturdayClasses(): List<SaturdayClass>? {
        var saturdayClasses: List<SaturdayClass>
        withContext(Dispatchers.IO) {
            saturdayClasses = timetableDao.getAllSaturdayClasses()
        }
        return saturdayClasses
    }

    override suspend fun getAllSundayClasses(): List<SundayClass>? {
        var sundayClasses: List<SundayClass>
        withContext(Dispatchers.IO) {
            sundayClasses = timetableDao.getAllSundayClasses()
        }
        return sundayClasses
    }

    override suspend fun getMondayClassWithId(id: Long): MondayClass {
        var mondayClass: MondayClass
        withContext(Dispatchers.IO) {
            mondayClass = timetableDao.getMondayClassById(id)
        }
        return mondayClass
    }

    override suspend fun getTuesdayClassWithId(id: Long): TuesdayClass {
        var tuesdayClass: TuesdayClass
        withContext(Dispatchers.IO) {
            tuesdayClass = timetableDao.getTuesdayClassById(id)
        }
        return tuesdayClass
    }

    override suspend fun getWednesdayClassWithId(id: Long): WednesdayClass {
        var wednesdayClass: WednesdayClass
        withContext(Dispatchers.IO) {
            wednesdayClass = timetableDao.getWednesdayClassById(id)
        }
        return wednesdayClass
    }

    override suspend fun getThursdayClassWithId(id: Long): ThursdayClass {
        var thursdayClass: ThursdayClass
        withContext(Dispatchers.IO) {
            thursdayClass = timetableDao.getThursdayClassById(id)
        }
        return thursdayClass
    }

    override suspend fun getFridayClassWithId(id: String): FridayClass {
        var fridayClass: FridayClass
        withContext(Dispatchers.IO) {
            fridayClass = timetableDao.getFridayClassById(id)
        }
        return fridayClass
    }

    override suspend fun getSaturdayClassWithId(id: Long): SaturdayClass {
        var saturdayClass: SaturdayClass
        withContext(Dispatchers.IO) {
            saturdayClass = timetableDao.getSaturdayClassById(id)
        }
        return saturdayClass
    }

    override suspend fun getSundayClassWithId(id: Long): SundayClass {
        var sundayClass: SundayClass
        withContext(Dispatchers.IO) {
            sundayClass = timetableDao.getSundayClassById(id)
        }
        return sundayClass
    }
}