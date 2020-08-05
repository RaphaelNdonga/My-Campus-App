package com.example.android.mycampusapp.timetable.input.tuesday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.timetable.data.TuesdayClass
import com.google.firebase.firestore.FirebaseFirestore

class TuesdayInputViewModelFactory(
    private val firestore: FirebaseFirestore,
    private val tuesdayClass: TuesdayClass?,
    private val app:Application
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TuesdayInputViewModel(
            firestore,
            tuesdayClass,
            app
        ) as T
    }
}