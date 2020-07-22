package com.example.android.mycampusapp.input.thursday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.data.ThursdayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource

class ThursdayInputViewModelFactory(
    private val timetableRepository: TimetableDataSource,
    private val thursdayClass: ThursdayClass?,
    private val app:Application
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ThursdayInputViewModel(timetableRepository, thursdayClass, app) as T
    }
}