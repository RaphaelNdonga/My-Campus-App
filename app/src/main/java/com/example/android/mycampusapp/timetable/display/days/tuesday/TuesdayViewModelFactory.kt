package com.example.android.mycampusapp.timetable.display.days.tuesday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore

class TuesdayViewModelFactory(private val firestore: FirebaseFirestore):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return TuesdayViewModel(
            firestore
        ) as T
    }
}