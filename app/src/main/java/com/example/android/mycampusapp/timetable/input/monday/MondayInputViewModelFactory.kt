package com.example.android.mycampusapp.timetable.input.monday

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.mycampusapp.timetable.data.MondayClass
import com.google.firebase.firestore.FirebaseFirestore

class MondayInputViewModelFactory(
    private val firestore: FirebaseFirestore,
    private val mondayClass: MondayClass?,
    private val app:Application
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MondayInputViewModel(
            firestore,
            mondayClass,
            app
        ) as T
    }
}