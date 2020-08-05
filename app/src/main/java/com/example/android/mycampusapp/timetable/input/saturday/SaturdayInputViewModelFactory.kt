package com.example.android.mycampusapp.timetable.input.saturday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.timetable.data.SaturdayClass
import com.google.firebase.firestore.FirebaseFirestore

class SaturdayInputViewModelFactory(
    private val firestore: FirebaseFirestore,
    private val saturdayClass: SaturdayClass?,
    private val app:Application
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SaturdayInputViewModel(
            firestore,
            saturdayClass,
            app
        ) as T
    }
}