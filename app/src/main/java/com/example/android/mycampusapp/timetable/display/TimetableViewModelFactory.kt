package com.example.android.mycampusapp.timetable.display

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.util.DayOfWeek
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.functions.FirebaseFunctions

class TimetableViewModelFactory(
    private val dayCollection: CollectionReference,
    private val functions: FirebaseFunctions,
    private val dayOfWeek: DayOfWeek,
    private val app: Application
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return TimetableViewModel(
            dayCollection, functions, dayOfWeek, app
        ) as T
    }
}