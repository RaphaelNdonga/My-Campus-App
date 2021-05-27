package com.mycampusapp.timetable.input

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.functions.FirebaseFunctions
import com.mycampusapp.data.TimetableClass
import com.mycampusapp.util.DayOfWeek

class TimetableInputViewModelFactory(
    private val fridayClass: TimetableClass?,
    private val app:Application,
    private val dayCollection: CollectionReference,
    private val functions:FirebaseFunctions,
    private val dayOfWeek: DayOfWeek
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TimetableInputViewModel(
            fridayClass,
            app,
            dayCollection,
            functions,
            dayOfWeek
        ) as T
    }
}