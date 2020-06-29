package com.example.android.mycampusapp.data.timetable.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.data.*

class FakeTimetableRepository:TimetableDataSource {

    private val mondayClasses: LinkedHashMap<Long, MondayClass> = LinkedHashMap()
    private val observableMondayClass = MutableLiveData<List<MondayClass>>()

    override suspend fun addMondayClass(mondayClass: MondayClass) {
        mondayClasses[mondayClass.id] = mondayClass
    }

    override suspend fun addTuesdayClass(tuesdayClass: TuesdayClass) {
        TODO("Not yet implemented")
    }

    override suspend fun addWednesdayClass(wednesdayClass: WednesdayClass) {
        TODO("Not yet implemented")
    }

    override suspend fun addThursdayClass(thursdayClass: ThursdayClass) {
        TODO("Not yet implemented")
    }

    override suspend fun addFridayClass(fridayClass: FridayClass) {
        TODO("Not yet implemented")
    }

    override suspend fun addSaturdayClass(saturdayClass: SaturdayClass) {
        TODO("Not yet implemented")
    }

    override suspend fun addSundayClass(sundayClass: SundayClass) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMondayClass(mondayClass: MondayClass) {
        mondayClasses.remove(mondayClass.id)
    }

    override suspend fun deleteTuesdayClass(tuesdayClass: TuesdayClass) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWednesdayClass(wednesdayClass: WednesdayClass) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteThursdayClass(thursdayClass: ThursdayClass) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFridayClass(fridayClass: FridayClass) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSaturdayClass(saturdayClass: SaturdayClass) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSundayClass(sundayClass: SundayClass) {
        TODO("Not yet implemented")
    }

    override suspend fun updateMondayClass(mondayClass: MondayClass) {
        mondayClasses.remove(mondayClass.id)
        mondayClasses[mondayClass.id] = mondayClass
    }

    override suspend fun updateTuesdayClass(tuesdayClass: TuesdayClass) {
        TODO("Not yet implemented")
    }

    override suspend fun updateWednesdayClass(wednesdayClass: WednesdayClass) {
        TODO("Not yet implemented")
    }

    override suspend fun updateThursdayClass(thursdayClass: ThursdayClass) {
        TODO("Not yet implemented")
    }

    override suspend fun updateFridayClass(fridayClass: FridayClass) {
        TODO("Not yet implemented")
    }

    override suspend fun updateSaturdayClass(saturdayClass: SaturdayClass) {
        TODO("Not yet implemented")
    }

    override suspend fun updateSundayClass(sundayClass: SundayClass) {
        TODO("Not yet implemented")
    }

    override suspend fun observeAllMondayClasses(): LiveData<List<MondayClass>> {
        observableMondayClass.value = mondayClasses.values.toList()
        return observableMondayClass
    }

    override suspend fun observeAllTuesdayClasses(): LiveData<List<TuesdayClass>> {
        TODO("Not yet implemented")
    }

    override suspend fun observeAllWednesdayClasses(): LiveData<List<WednesdayClass>> {
        TODO("Not yet implemented")
    }

    override suspend fun observeAllThursdayClasses(): LiveData<List<ThursdayClass>> {
        TODO("Not yet implemented")
    }

    override suspend fun observeAllFridayClasses(): LiveData<List<FridayClass>> {
        TODO("Not yet implemented")
    }

    override suspend fun observeAllSaturdayClasses(): LiveData<List<SaturdayClass>> {
        TODO("Not yet implemented")
    }

    override suspend fun observeAllSundayClasses(): LiveData<List<SundayClass>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllMondayClasses() {
        mondayClasses.clear()
    }

    override suspend fun deleteAllTuesdayClasses() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllWednesdayClasses() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllThursdayClasses() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllFridayClasses() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllSaturdayClasses() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllSundayClasses() {
        TODO("Not yet implemented")
    }

    override suspend fun getAllMondayClasses(): List<MondayClass>? {
        return mondayClasses.values.toList()
    }

    override suspend fun getAllTuesdayClasses(): List<TuesdayClass>? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllWednesdayClasses(): List<WednesdayClass>? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllThursdayClasses(): List<ThursdayClass>? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllFridayClasses(): List<FridayClass>? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllSaturdayClasses(): List<SaturdayClass>? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllSundayClasses(): List<SundayClass>? {
        TODO("Not yet implemented")
    }

}
