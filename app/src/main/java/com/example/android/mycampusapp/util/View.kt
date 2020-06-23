package com.example.android.mycampusapp.util

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.android.mycampusapp.Event
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(snackbarText:String,timeLength:Int){
    Snackbar.make(this, snackbarText,timeLength).run {
        show()
    }
}

fun View.setupSnackbar(lifecycleOwner: LifecycleOwner,snackbarEvent:LiveData<Event<String>>,timeLength: Int){
    snackbarEvent.observe(lifecycleOwner, Observer { event->
        event.getContentIfNotHandled()?.let {
            showSnackbar(it,timeLength)
        }
    })
}