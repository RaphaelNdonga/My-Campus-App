package com.example.android.mycampusapp.timetable.input.thursday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.timetable.data.ThursdayClass
import com.google.firebase.firestore.FirebaseFirestore

class ThursdayInputViewModelFactory(
    private val firestore: FirebaseFirestore,
    private val thursdayClass: ThursdayClass?,
    private val app:Application
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ThursdayInputViewModel(
            firestore,
            thursdayClass,
            app
        ) as T
    }
}