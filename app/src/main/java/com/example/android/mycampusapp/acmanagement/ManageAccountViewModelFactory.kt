package com.example.android.mycampusapp.acmanagement

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ManageAccountViewModelFactory(
    private val app: Application
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ManageAccountViewModel(app) as T
    }
}