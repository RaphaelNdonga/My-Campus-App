package com.example.android.mycampusapp.input.wednesday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.data.WednesdayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource

class WednesdayInputViewModelFactory(
    private val timetableRepository: TimetableDataSource,
    private val wednesdayClass: WednesdayClass?,
    private val app:Application
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WednesdayInputViewModel(timetableRepository, wednesdayClass, app) as T
    }
}