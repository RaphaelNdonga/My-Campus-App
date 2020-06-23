package com.example.android.mycampusapp.data.timetable.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.mycampusapp.data.*
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

class TimetableLocalDataSource @Inject constructor(private val timetableDao: TimetableDao) :
    TimetableDataSource {

    val job = Job()
    private val uiScope = CoroutineScope(job + Dispatchers.Main)
    override fun addMondayClass(mondayClass: MondayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.insert(mondayClass)
            }
        }
    }

    override fun addTuesdayClass(tuesdayClass: TuesdayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.insert(tuesdayClass)
            }
        }
    }

    override fun addWednesdayClass(wednesdayClass: WednesdayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.insert(wednesdayClass)
            }
        }
    }

    override fun addThursdayClass(thursdayClass: ThursdayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.insert(thursdayClass)
            }
        }
    }

    override fun addFridayClass(fridayClass: FridayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.insert(fridayClass)
            }
        }
    }

    override fun addSaturdayClass(saturdayClass: SaturdayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.insert(saturdayClass)
            }
        }
    }

    override fun addSundayClass(sundayClass: SundayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.insert(sundayClass)
            }
        }
    }

    override fun deleteMondayClass(mondayClass: MondayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.delete(mondayClass)
            }
        }
    }

    override fun deleteTuesdayClass(tuesdayClass: TuesdayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.delete(tuesdayClass)
            }
        }
    }

    override fun deleteWednesdayClass(wednesdayClass: WednesdayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.delete(wednesdayClass)
            }
        }
    }

    override fun deleteThursdayClass(thursdayClass: ThursdayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.delete(thursdayClass)
            }
        }
    }

    override fun deleteFridayClass(fridayClass: FridayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.delete(fridayClass)
            }
        }
    }

    override fun deleteSaturdayClass(saturdayClass: SaturdayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.delete(saturdayClass)
            }
        }
    }

    override fun deleteSundayClass(sundayClass: SundayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.delete(sundayClass)
            }
        }
    }

    override fun updateMondayClass(mondayClass: MondayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.update(mondayClass)
            }
        }
    }

    override fun updateTuesdayClass(tuesdayClass: TuesdayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.update(tuesdayClass)
            }
        }
    }

    override fun updateWednesdayClass(wednesdayClass: WednesdayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.update(wednesdayClass)
            }
        }
    }

    override fun updateThursdayClass(thursdayClass: ThursdayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.update(thursdayClass)
            }
        }
    }

    override fun updateFridayClass(fridayClass: FridayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.update(fridayClass)
            }
        }
    }

    override fun updateSaturdayClass(saturdayClass: SaturdayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.update(saturdayClass)
            }
        }
    }

    override fun updateSundayClass(sundayClass: SundayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.update(sundayClass)
            }
        }
    }

    override fun observeAllMondayClasses(): LiveData<List<MondayClass>> {
        val mondayList = MutableLiveData<List<MondayClass>>()
        uiScope.launch {
            withContext(Dispatchers.IO) {
                mondayList.value = timetableDao.observeAllMondayClasses().value
            }
        }
        return mondayList
    }

    override fun observeAllTuesdayClasses(): LiveData<List<TuesdayClass>> {
        val tuesdayList = MutableLiveData<List<TuesdayClass>>()
        uiScope.launch {
            withContext(Dispatchers.IO) {
                tuesdayList.value = timetableDao.observeAllTuesdayClasses().value
            }
        }
        return tuesdayList
    }

    override fun observeAllWednesdayClasses(): LiveData<List<WednesdayClass>> {
        val wednesdayList = MutableLiveData<List<WednesdayClass>>()
        uiScope.launch {
            withContext(Dispatchers.IO) {
                wednesdayList.value = timetableDao.observeAllWednesdayClasses().value
            }
        }
        return wednesdayList
    }

    override fun observeAllThursdayClasses(): LiveData<List<ThursdayClass>> {
        val thursdayList = MutableLiveData<List<ThursdayClass>>()
        uiScope.launch {
            withContext(Dispatchers.IO) {
                thursdayList.value = timetableDao.observeAllThursdayClasses().value
            }
        }
        return thursdayList
    }

    override fun observeAllFridayClasses(): LiveData<List<FridayClass>> {
        val fridayList = MutableLiveData<List<FridayClass>>()
        uiScope.launch {
            withContext(Dispatchers.IO) {
                fridayList.value = timetableDao.observeAllFridayClasses().value
            }
        }
        return fridayList
    }

    override fun observeAllSaturdayClasses(): LiveData<List<SaturdayClass>> {
        val saturdayList = MutableLiveData<List<SaturdayClass>>()
        uiScope.launch {
            withContext(Dispatchers.IO) {
                saturdayList.value = timetableDao.observeAllSaturdayClasses().value
            }
        }
        return saturdayList
    }

    override fun observeAllSundayClasses(): LiveData<List<SundayClass>> {
        val sundayList = MutableLiveData<List<SundayClass>>()
        uiScope.launch {
            withContext(Dispatchers.IO) {
                sundayList.value = timetableDao.observeAllSundayClasses().value
            }
        }
        return sundayList
    }


    override fun deleteAllMondayClasses(mondayClass: MondayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.delete(mondayClass)
            }
        }
    }

    override fun deleteAllTuesdayClasses(tuesdayClass: TuesdayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.update(tuesdayClass)
            }
        }
    }

    override fun deleteAllWednesdayClasses(wednesdayClass: WednesdayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.update(wednesdayClass)
            }
        }
    }

    override fun deleteAllThursdayClasses(thursdayClass: ThursdayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.update(thursdayClass)
            }
        }
    }

    override fun deleteAllFridayClasses(fridayClass: FridayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.update(fridayClass)
            }
        }
    }

    override fun deleteAllSaturdayClasses(saturdayClass: SaturdayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.update(saturdayClass)
            }
        }
    }

    override fun deleteAllSundayClasses(sundayClass: SundayClass) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                timetableDao.update(sundayClass)
            }
        }
    }

    //TODO Remove this code once you're done testing the database
    override fun getMondayClassWithId(id:Long):MondayClass?{
        var mondayClass:MondayClass? = null
        uiScope.launch {
            withContext(Dispatchers.IO){
                mondayClass = timetableDao.getMondayClassById(id)
            }
        }
        return mondayClass
    }


}