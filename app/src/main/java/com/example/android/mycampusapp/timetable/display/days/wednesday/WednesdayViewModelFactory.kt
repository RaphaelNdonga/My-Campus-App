package com.example.android.mycampusapp.timetable.display.days.wednesday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore

class WednesdayViewModelFactory(private val firestore: FirebaseFirestore):ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return WednesdayViewModel(
            firestore
        ) as T
    }
}