package com.example.android.mycampusapp.timetable.display.days.saturday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.DocumentReference

class SaturdayViewModelFactory(private val courseCollection: DocumentReference):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SaturdayViewModel(
            courseCollection
        ) as T
    }
}