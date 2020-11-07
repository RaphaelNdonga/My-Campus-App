package com.example.android.mycampusapp.timetable.display.days.saturday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.DocumentReference

class SaturdayViewModelFactory(
    private val courseCollection: DocumentReference,
    private val app: Application
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SaturdayViewModel(
            courseCollection,app
        ) as T
    }
}