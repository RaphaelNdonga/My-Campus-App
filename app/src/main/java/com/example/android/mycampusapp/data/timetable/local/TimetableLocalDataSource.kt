package com.example.android.mycampusapp.data.timetable.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.data.*
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

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

    override suspend fun getAllMondayClasses():List<MondayClass>? {
        var mondayClasses:List<MondayClass>? = null
        withContext(Dispatchers.IO){
            mondayClasses = timetableDao.getAllMondayClasses()
        }
        return mondayClasses
    }
    override suspend fun getAllTuesdayClasses():List<TuesdayClass>? {
        var tuesdayClasses:List<TuesdayClass>? = null
        withContext(Dispatchers.IO){
            tuesdayClasses = timetableDao.getAllTuesdayClasses()
        }
        return tuesdayClasses
    }
    override suspend fun getAllWednesdayClasses():List<WednesdayClass>? {
        var wednesdayClasses:List<WednesdayClass>? = null
        withContext(Dispatchers.IO){
            wednesdayClasses = timetableDao.getAllWednesdayClasses()
        }
        return wednesdayClasses
    }
    override suspend fun getAllThursdayClasses():List<ThursdayClass>? {
        var thursdayClasses:List<ThursdayClass>? = null
        withContext(Dispatchers.IO){
            thursdayClasses = timetableDao.getAllThursdayClasses()
        }
        return thursdayClasses
    }
    override suspend fun getAllFridayClasses():List<FridayClass>? {
        var fridayClasses:List<FridayClass>? = null
        withContext(Dispatchers.IO){
            fridayClasses = timetableDao.getAllFridayClasses()
        }
        return fridayClasses
    }
    override suspend fun getAllSaturdayClasses():List<SaturdayClass>? {
        var saturdayClasses:List<SaturdayClass>? = null
        withContext(Dispatchers.IO){
            saturdayClasses = timetableDao.getAllSaturdayClasses()
        }
        return saturdayClasses
    }
    override suspend fun getAllSundayClasses():List<SundayClass>? {
        var sundayClasses:List<SundayClass>? = null
        withContext(Dispatchers.IO){
            sundayClasses = timetableDao.getAllSundayClasses()
        }
        return sundayClasses
    }
}