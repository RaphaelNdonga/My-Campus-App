package com.example.android.mycampusapp.timetable.input.wednesday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.timetable.data.WednesdayClass
import com.google.firebase.firestore.FirebaseFirestore

class WednesdayInputViewModelFactory(
    private val firestore: FirebaseFirestore,
    private val wednesdayClass: WednesdayClass?,
    private val app:Application
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WednesdayInputViewModel(
            firestore,
            wednesdayClass,
            app
        ) as T
    }
}