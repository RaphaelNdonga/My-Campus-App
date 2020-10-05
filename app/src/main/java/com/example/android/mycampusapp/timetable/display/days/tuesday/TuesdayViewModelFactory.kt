package com.example.android.mycampusapp.timetable.display.days.tuesday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.DocumentReference

class TuesdayViewModelFactory(private val courseDocument:DocumentReference):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return TuesdayViewModel(
            courseDocument
        ) as T
    }
}